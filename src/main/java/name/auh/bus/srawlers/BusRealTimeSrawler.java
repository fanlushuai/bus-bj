package name.auh.bus.srawlers;

import cn.wanghaomiao.seimi.annotation.Crawler;
import cn.wanghaomiao.seimi.struct.Request;
import cn.wanghaomiao.seimi.struct.Response;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import name.auh.bus.common.BusRealTimeRet;
import name.auh.bus.data.bean.BusRealTime;
import name.auh.bus.data.bean.BusRealTimeId;
import name.auh.bus.data.repository.BusRealTimeRepository;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Crawler(name = "BusRealTime", useUnrepeated = false)
@Slf4j
public class BusRealTimeSrawler extends BaseCrawLer {

    public static final String URL = "http://www.bjbus.com/home/ajax_rtbus_data.php?act=busTime&selBLine=2&selBDir=5702371011268233799&selBStop=3";

    public static final String XPATH = "//article/p/text()";

    @Autowired
    BusRealTimeRepository busRealTimeRepository;

    public static Request buildRequest(String line, String dir, String stop) {
        Request request = Request.build(URL, "parse").setCrawlerName("BusRealTime");
        request.getMeta().put("line", line);
        request.getMeta().put("dir", dir);
        request.getMeta().put("stop", stop);
        return request;
    }

    public void parse(Response response) {
        String line = (String) response.getMeta().get("line");
        String dir = (String) response.getMeta().get("dir");
        String stop = (String) response.getMeta().get("stop");

        BusRealTimeRet busRealTimeRet = JSON.parseObject(response.getContent(), BusRealTimeRet.class);
        List<JXNode> jxNode = JXDocument.create(busRealTimeRet.getHtml()).selN(XPATH);
        if (CollectionUtils.isEmpty(jxNode)) {
            log.error("响应错误");
            return;
        }

        String runTime = jxNode.get(0).asString();
        String status = jxNode.get(1).asString();

        BusRealTimeId busRealTimeId = new BusRealTimeId(line, dir, stop);
        if (busRealTimeRepository.existsById(busRealTimeId)) {
            busRealTimeRepository.deleteById(busRealTimeId);
        }
        busRealTimeRepository.save(new BusRealTime(runTime, status, line, dir, stop, null, System.currentTimeMillis()));

        log.info("BusRealTime {} {} {} >>> {} {}", line, dir, stop, runTime, status);
    }

}
