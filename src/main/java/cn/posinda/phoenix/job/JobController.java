package cn.posinda.phoenix.job;

import cn.posinda.base.WebResult;
import cn.posinda.phoenix.job.importConcerned.service.ImportFailureService;
import cn.posinda.utils.Lists;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@RestController
@RequestMapping("/job")
public class JobController {

    @Qualifier("taskScheduler")
    @Resource
    private ThreadPoolTaskScheduler scheduler;

    private ConcurrentHashMap<String, ScheduledFuture<?>> map = new ConcurrentHashMap<>();

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @Resource
    private TableInfoService tableInfoService;

    @Resource
    private ImportFailureService importFailureService;

    /**
     * sqoop导入的时候最好自己手动在hbase创建表，之后在phoenix中创建对应的表,并在TBL_TABLE_INFO表中插入相关的属性信息,之后再导入数据
     * 执行完成sqoop导入之后,不需要等待任务的完成,调用这个方法,会根据传入的任务名称启动一个定时任务,
     * 每三分钟查询一次mapreduce作业历史服务器,如果查询到该作业的状态为成功,那么会查找出插入的数据条数,
     * 同时将查询到的记录条数存储到相应的表并停止该定时任务,如果任务的状态为RUNNING,则三分钟以后再次进行查询
     * 如果是 FAILED, KILL_WAIT, KILLED, ERROR状态则直接停止定时任务
     * 注意：该查询结果为从对应的数据库读取的数据的条数,不一定和hbase表的实际存储条数相等,因为可能存在更新覆盖的情况,如果不存在更新的情况,两者的值相等
     * 任务名称必须在数据平台上存在,表名称必须在phoenix中存在，任务名称必须和表名称对应，不然通过查询得出读取的记录条数会更新参数指定
     * 的表，引起结果异常
     *
     * @param jobName sqoop数据导入对应的mapreduce任务名称,为每一次任务的指定指定不同的任务名称，比如表名，如果任务失败执行相同的命令
     *                最好指定不同的名称，比如表名+"-"+尝试的次数
     */
    @RequestMapping(value = "/addJob", method = {RequestMethod.POST, RequestMethod.GET})
    public WebResult addJob(@RequestParam String jobName, @RequestParam String tableName) throws UnirestException {
        if (!tableInfoService.getAllTableNames().contains(tableName.trim()))
            throw new IllegalArgumentException("表名不存在");
        final JobDetail[] jobs = JobQuery.getAllJobs();
        JobDetail jobDetail = JobDetail.apply(jobName.trim());
        if (Lists.exists(jobDetail, jobs)) throw new IllegalArgumentException("数据平台上不存在对应的任务");
        Date date = new Date();
        String dateString = format.format(date);
        JobRunnable runnable = new JobRunnable(dateString, jobName, map, tableName);
        runnable.setTableInfoService(tableInfoService);
        runnable.setImportFailureService(importFailureService);
        ScheduledFuture<?> scheduledFuture = scheduler.schedule(runnable, new CronTrigger("0 */3 * * * ?"));
        if (Objects.nonNull(scheduledFuture)) map.put(dateString, scheduledFuture);
        return WebResult.success();
    }

}
