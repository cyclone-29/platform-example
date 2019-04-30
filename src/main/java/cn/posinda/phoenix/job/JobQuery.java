package cn.posinda.phoenix.job;


import cn.posinda.utils.package$;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.http.MediaType;

@SuppressWarnings("all")
public class JobQuery {

    private static final Gson gson = new Gson();

    private static final JsonParser parser = new JsonParser();

    private static String jobListUrl = new StringBuilder("http://").append(package$.MODULE$.configuration().get("mapreduce.jobhistory.webapp.address")).append("/ws/v1/history/mapreduce/jobs").toString();

    private static String specifiedJobCountersUrl = new StringBuilder("http://").append(package$.MODULE$.configuration().get("mapreduce.jobhistory.webapp.address")).append("/ws/v1/history/mapreduce/jobs/").toString();

    /**
     * 查询出所有的任务实例
     *
     * @return
     * @throws UnirestException
     */
    public static JobDetail[] getAllJobs() throws UnirestException {
        HttpResponse<JsonNode> response = Unirest.get(jobListUrl).header("accept", MediaType.APPLICATION_JSON_UTF8_VALUE).asJson();
        int statusCode;
        if ((statusCode = response.getStatus()) != 200) throw new UnirestException("数据请求异常,异常code:" + statusCode);
        String jobs = response.getBody().getObject().toString();
        final JsonObject parse = (JsonObject) parser.parse(jobs);
        return gson.fromJson(parse.getAsJsonObject("jobs").getAsJsonArray("job"), JobDetail[].class);
    }

    /**
     * 根据任务名称查询到指定的任务实例
     *
     * @param jobArr  平台上运行的所有任务
     * @param jobName 任务名称
     * @return
     */
    public static JobDetail getJobDetail(JobDetail[] jobArr, String jobName) {
        return jobArr[JobDetail$.MODULE$.getIndex(jobArr, jobName)];
    }

    private static String makeQualifiedForCounterQuery(String jobId) {
        return specifiedJobCountersUrl.concat(jobId).concat("/counters");
    }

    public static Long getInputRecords(String jobId) throws UnirestException {
        final String specifiedJobDetailUrl = makeQualifiedForCounterQuery(jobId);
        final HttpResponse<JsonNode> response = Unirest.get(specifiedJobDetailUrl).header("accept", MediaType.APPLICATION_JSON_UTF8_VALUE).asJson();
        final String string = response.getBody().getObject().toString();
        final JsonObject parse = (JsonObject) parser.parse(string);
        return parse.getAsJsonObject("jobCounters").getAsJsonArray("counterGroup").get(2).getAsJsonObject().
                getAsJsonArray("counter").get(0).getAsJsonObject().get("totalCounterValue").getAsLong();
    }

    public enum Status {

        SUCCEEDED("SUCCEEDED"), FAILED("FAILED"), KILL_WAIT("KILL_WAIT"), KILLED("KILLED"), ERROR("ERROR");

        private String state;

        Status(String state) {
            this.state = state;
        }

        @Override
        public String toString() {
            return this.state;
        }
    }
}
