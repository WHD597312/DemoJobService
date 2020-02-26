package com.supcon.mes.daemonservice;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

@RequiresApi(api= Build.VERSION_CODES.LOLLIPOP)
public class JobSchedulerService  extends JobService {

    private static final String  TAG="JobService";
    private static final int JOB_ID=10000;

    public static void scheduleJobService(Context context){
        boolean isSuccess=false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, new ComponentName(context, JobSchedulerService.class));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setMinimumLatency(DaemonUtil.getIntervalTime());
                builder.setOverrideDeadline(DaemonUtil.getIntervalTime() * 2);
                builder.setBackoffCriteria(DaemonUtil.getIntervalTime(), JobInfo.BACKOFF_POLICY_LINEAR);//线性重试方案
            } else {
                builder.setPeriodic(DaemonUtil.getIntervalTime());
            }
            builder.setPersisted(true);
            JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            if (jobScheduler != null) {
                jobScheduler.cancelAll();
                isSuccess = jobScheduler.schedule(builder.build()) == JobScheduler.RESULT_SUCCESS;
            }
        }
        if (isSuccess) {
            Log.d(TAG, "Scheduler Success!");
        } else {
            Log.e(TAG, "Scheduler Failed!");
        }
    }

    /**
    *如果返回值是false,系统假设这个方法返回时任务已经执行完毕。
     * 如果返回值是true,那么系统假定这个任务正要被执行
     */
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG,"onStartJob");
        DaemonHolder.startService();
        return true;
    }

    /**
     *返回false,那么系统假定在接收到一个取消请求时已经没有正在运行的任务
     * 返回ture,取消正在等待执行的任务
     */
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(TAG,"onStopJob");
        DaemonHolder.stopService();
        return true;
    }
}
