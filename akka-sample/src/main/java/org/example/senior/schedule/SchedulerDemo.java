package org.example.senior.schedule;

import akka.actor.*;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;


public class SchedulerDemo {
    static class SenderActor extends AbstractActor {
        @Override
        public Receive createReceive() {
            return receiveBuilder().build();
        }
    }

    static class TargetActor extends AbstractActor {
        @Override
        public Receive createReceive() {
            return receiveBuilder().matchAny(msg -> {
                System.out.println(getSender() + "===>" + msg);
            }).build();
        }
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys");

        ActorRef sender = system.actorOf(Props.create(SenderActor.class), "sender");
        ActorRef target = system.actorOf(Props.create(TargetActor.class), "target");

        // 延迟 3 秒之后给 refTarget 发送一条消息(“hello”)
        system.scheduler().scheduleOnce(Duration.create(3, "s"), target, "hello", system.dispatcher(), sender);


        // 延迟 1 秒之后给 refTarget 发送一条消息（“hello”），然后每隔 3 秒执行一次（发消息）。在不需要该定时任务时，可以调用 cancel 方法将其取消。
        Cancellable cancelHandler = system.scheduler().schedule(Duration.create(1, "s"), Duration.create(3, "s"), target, "hello", system.dispatcher(), sender);

        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        cancelHandler.cancel();
    }
}
