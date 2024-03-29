package org.example.senior.route;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.FromConfig;
import com.typesafe.config.ConfigFactory;


public class BroadcastGroupDemo {

    static class Worker1 extends AbstractActor {
        @Override
        public Receive createReceive() {
            return receiveBuilder().matchAny(msg -> {
                System.out.println("Worker1" + "-->" + msg);
            }).build();
        }
    }

    static class Worker2 extends AbstractActor {

        @Override
        public Receive createReceive() {
            return receiveBuilder().matchAny(msg -> {
                System.out.println("Worker2" + "-->" + msg);
            }).build();
        }

    }

    static class RouterActor extends AbstractActor {

        private ActorRef router;

        @Override

        public void preStart() throws Exception {
            getContext().actorOf(Props.create(Worker1.class), "wk1");
            getContext().actorOf(Props.create(Worker2.class), "wk2");
            router = getContext().actorOf(FromConfig.getInstance().props(), "routerActor");
        }

        @Override
        public Receive createReceive() {
            return receiveBuilder().matchAny(msg -> {
                router.tell(msg, getSender());
            }).build();
        }
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys", ConfigFactory.load("router.conf"));
        ActorRef master = system.actorOf(Props.create(RouterActor.class), "broadcastGroupActor");
        master.tell("helloA", ActorRef.noSender());
        master.tell("helloB", ActorRef.noSender());
    }
}
