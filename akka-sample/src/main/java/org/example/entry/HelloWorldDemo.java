package org.example.entry;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Creator;


public class HelloWorldDemo extends AbstractActor {

//    // 可以通过工厂方式创建actor, 一次模板，到处创建
    public static Props createProps() {
        return Props.create(new Creator<Actor>() {
            @Override
            public Actor create() throws Exception {
                return new HelloWorldDemo();
            }
        });
    }

    private LoggingAdapter log = Logging.getLogger(this.getContext().getSystem(), this);


    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .matchAny(o -> {
                    log.info("any" + o.toString());
                })
                .matchEquals("hello", s -> log.info("equals"+s))
                .match(
                        String.class,
                        s -> log.info(s))

                .build();
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys");
        ActorRef actorRef = system.actorOf(HelloWorldDemo.createProps(), "actorDemo");
        actorRef.tell("hello", ActorRef.noSender()); // ActorRef.noSender()实际上就是叫做deadLetters的actor
    }
}
