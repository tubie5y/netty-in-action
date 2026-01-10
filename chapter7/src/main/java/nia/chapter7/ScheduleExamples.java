package nia.chapter7;

import io.netty.channel.Channel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 代码清单 7-2 使用 ScheduledExecutorService 调度任务
 *
 * 代码清单 7-3 使用 EventLoop 调度任务
 *
 * 代码清单 7-4 使用 EventLoop 调度周期性的任务
 *
 * 代码清单 7-5 使用 ScheduledFuture 取消任务
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class ScheduleExamples {
    private static final Channel CHANNEL_FROM_SOMEWHERE = new NioSocketChannel();

    /**
     * 代码清单 7-2 使用 ScheduledExecutorService 调度任务
     *      代码清单7-2展示了如何使用ScheduledExecutorService来在60秒的延迟之后执行一个任务。
     */
    public static void schedule() {
        //创建一个其线程池具有 10 个线程的ScheduledExecutorService
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);

        ScheduledFuture<?> future = executor.schedule(
            //创建一个 Runnable，以供调度稍后执行
            new Runnable() {
            @Override
            public void run() {
                //该任务要打印的消息
                System.out.println("Now it is 60 seconds later");
            }
        //调度任务在从现在开始的 60 秒之后执行
        }, 60, TimeUnit.SECONDS);
        //...
        //一旦调度任务执行完成，就关闭 ScheduledExecutorService 以释放资源
        executor.shutdown();
    }

    /**
     * 代码清单 7-3 使用 EventLoop 调度任务
     *      - ScheduledExecutorService的实现具有局限性，例如，事实上作为线程池管理的一部分，将会有额外的线程创建。如果有大量任务被紧凑地调度，那么这将成为一个瓶颈。
     *        Netty通过Channel的EventLoop实现任务调度解决了这一问题，如代码清单7-3所示。
     *
     *      - 经过60秒之后，Runnable实例将由分配给Channel的EventLoop执行。如果要调度任务以每隔60秒执行一次，请使用scheduleAtFixedRate()方法，如代码清单7-4所示。
     */
    public static void scheduleViaEventLoop() {
        Channel ch = CHANNEL_FROM_SOMEWHERE; // get reference from somewhere
        ScheduledFuture<?> future = ch.eventLoop().schedule(
            //创建一个 Runnable以供调度稍后执行
            new Runnable() {
            @Override
            public void run() {
                //要执行的代码
                System.out.println("60 seconds later");
            }
            //调度任务在从现在开始的 60 秒之后执行
        }, 60, TimeUnit.SECONDS);
    }

    /**
     * 代码清单 7-4 使用 EventLoop 调度周期性的任务
     * */
    public static void scheduleFixedViaEventLoop() {
        Channel ch = CHANNEL_FROM_SOMEWHERE; // get reference from somewhere
        ScheduledFuture<?> future = ch.eventLoop().scheduleAtFixedRate(
           //创建一个 Runnable，以供调度稍后执行
           new Runnable() {
           @Override
           public void run() {
                //这将一直运行，直到 ScheduledFuture 被取消
                System.out.println("Run every 60 seconds");
           }
        //调度在 60 秒之后，并且以后每间隔 60 秒运行
        }, 60, 60, TimeUnit.SECONDS);
    }

    /**
     * 代码清单 7-5 使用 ScheduledFuture 取消任务
     *      要想取消或者检查（被调度任务的）执行状态，可以使用每个异步操作所返回的ScheduledFuture。代码清单7-5展示了一个简单的取消操作。
     */
    public static void cancelingTaskUsingScheduledFuture() {
        Channel ch = CHANNEL_FROM_SOMEWHERE; // get reference from somewhere
        //...
        //调度任务，并获得所返回的ScheduledFuture
        ScheduledFuture<?> future = ch.eventLoop().scheduleAtFixedRate(
                new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Run every 60 seconds");
                    }
                }, 60, 60, TimeUnit.SECONDS);
        // Some other code that runs...
        boolean mayInterruptIfRunning = false;
        //取消该任务，防止它再次运行
        future.cancel(mayInterruptIfRunning);
    }
}
