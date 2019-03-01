package name.auh.bus.controller;

import cn.wanghaomiao.seimi.spring.common.CrawlerCache;
import cn.wanghaomiao.seimi.struct.CrawlerModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import name.auh.bus.common.Ret;
import name.auh.bus.data.repository.SrawlerRepository;
import name.auh.bus.srawlers.LineSrawler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(description = "抓取控制")
@RestController
@RequestMapping("/srawler/")
@Slf4j
public class SrawlerController {

    @Autowired
    SrawlerRepository srawlerRepository;

    @ApiOperation("启动某个抓取")
    @RequestMapping(value = "info", method = RequestMethod.GET)
    public Object line(@RequestParam("name") String name) {
        CrawlerModel model = CrawlerCache.getCrawlerModel(name);
        if (model == null) {
            return Ret.buildFailParam("not found" + name);
        }

        return Ret.buildSuc(model.queueInfo());
    }

    @ApiOperation("启动抓取，更新车站数据库资源")
    @RequestMapping(value = "line", method = RequestMethod.GET)
    public Object updateLineData(@ApiParam("简单验证") @RequestParam("key") String key) {
        if (!"xxxxxxy".equals(key)) {
            return Ret.buildSuc("正在执行……，fuck you！！！");
        }

        CrawlerModel model = CrawlerCache.getCrawlerModel("Line");
        if (model.getQueueInstance().len("Line") > 0) {
            return Ret.buildSuc("正在执行……，" + model.queueInfo());
        }

        CrawlerCache.consumeRequest(LineSrawler.buildLineRequest().setCrawlerName("Line"));
        return Ret.buildSuc("爬虫已经出发");
    }

}
