package org.example.senior.persist;

import akka.actor.*;
import akka.pattern.Patterns;
import akka.persistence.AbstractPersistentActor;
import akka.persistence.SnapshotOffer;
import akka.util.Timeout;
import com.typesafe.config.ConfigFactory;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;


public class PersistDemo {

    static class SimpleActor extends AbstractPersistentActor {
        private Integer count = 0;

        @Override
        public Receive createReceiveRecover() {
            return receiveBuilder().matchAny(msg -> {
                System.out.println(msg);
                if (msg.equals("add")) {
                    count++;
                } else if (msg instanceof SnapshotOffer) {
                    SnapshotOffer snapshotOffer = (SnapshotOffer) msg;
                    count = (Integer) snapshotOffer.snapshot();
                }
            }).build();
        }

        @Override
        public String persistenceId() {
            return "PersistDemo";
        }

        @Override
        public Receive createReceive() {
            return receiveBuilder().matchAny(msg -> {
                if (msg.equals("add")) {
                    persist(msg, param -> {
                        System.out.println("==========="+param);
                        count++;

                        if (count % 2 == 0) {
                            saveSnapshot(count);
                        }
                    });
                } else if(msg.equals("error")) {
                    throw new NullPointerException();
                } else if(msg.equals("get")) {
                    getSender().tell(count, getSelf());
                }
            }).build();
        }
    }

    public static void main(String[] args) throws Exception {
        ActorSystem system = ActorSystem.create("sys", ConfigFactory.load("persist.conf"));
        ActorRef ref = system.actorOf(Props.create(SimpleActor.class));

        ref.tell("add", ActorRef.noSender());
//        ref.tell("add", ActorRef.noSender());
//        ref.tell("error", ActorRef.noSender());
//        ref.tell("add", ActorRef.noSender());

        Timeout timeout = new Timeout(Duration.create(1, TimeUnit.SECONDS));
        Future<Object> future = Patterns.ask(ref, "get", timeout);
        System.out.println(Await.result(future, timeout.duration()));


    }
}
