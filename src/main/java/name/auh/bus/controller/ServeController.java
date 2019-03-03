package name.auh.bus.controller;

import cn.wanghaomiao.seimi.spring.common.CrawlerCache;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import name.auh.bus.common.LineInfoVo;
import name.auh.bus.common.Ret;
import name.auh.bus.data.bean.*;
import name.auh.bus.data.repository.BusRealTimeRepository;
import name.auh.bus.data.repository.DirectionRepository;
import name.auh.bus.data.repository.LineRepository;
import name.auh.bus.data.repository.StationRepository;
import name.auh.bus.service.BusService;
import name.auh.bus.srawlers.BusRealTimeSrawler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Api(description = "对外服务接口")
@RestController
@RequestMapping("/serve/")
@Slf4j
public class ServeController {

    @Autowired
    BusService busService;

    @Autowired
    LineRepository lineRepository;

    @Autowired
    DirectionRepository directionRepository;

    @Autowired
    StationRepository stationRepository;

    @Autowired
    BusRealTimeRepository busRealTimeRepository;

    @ApiOperation("获取所有车次")
    @GetMapping(value = "lines")
    public Object getLines() {
        return Ret.buildSuc(lineRepository.findAll());
    }

    @ApiOperation("获取所有车次 模糊查找（前20条）")
    @GetMapping("lines/{lineName}/like")
    public Object getLinesByFuzzyName(@ApiParam(value = "车次名称", example = "专60") @PathVariable("lineName") String lineName) {
        return Ret.buildSuc(lineRepository.findLinesByNameLike("%" + lineName + "%"));
    }

    @ApiOperation("获取车次的方向")
    @GetMapping(value = "lines/{lineName}/directions")
    public Object getDirectionOfLine(@ApiParam(value = "车次名称", example = "专60") @PathVariable("lineName") String lineName) {
        return Ret.buildSuc(directionRepository.findByLineEquals(lineName));
    }

    @ApiOperation("获取车次的车站 （同一车站正反向算作两个）")
    @GetMapping("stations/{line}")
    public Object getStationsByLine(@ApiParam(value = "车次名称", example = "专60") @PathVariable("line") String line,
                                    @RequestParam(value = "direction", required = false) String directionId) {
        if (StringUtils.isBlank(directionId)) {
            return Ret.buildSuc(stationRepository.findStationsByLine(line));

        }
        return Ret.buildSuc(stationRepository.findStationsByLineAndDir(line, directionId));
    }

    @ApiOperation("获取所有车站/某车次的车站，都为模糊查询")
    @GetMapping("stations/{stationName}/like")
    public Object getFuzzyStation(@PathVariable("stationName") String stationName, @ApiParam("不传所有车站的模糊查询，传单个车次的模糊查询") @RequestParam(value = "line", required = false) String line) {
        if (StringUtils.isBlank(line)) {
            return Ret.buildSuc(stationRepository.findStationsByNameLike(stationName));
        }

        if (lineRepository.existsById(line)) {
            return Ret.buildSuc(stationRepository.findStationsByNameLikeAndLineEquals(stationName, line));
        } else {
            return Ret.buildFailParam("车次不存在");
        }
    }

    @ApiOperation("获取车次的所有信息")
    @GetMapping("lines/{lineName}")
    public Object getLineInfo(@PathVariable("lineName") String lineName) {
        Optional<Line> line = lineRepository.findById(lineName);
        if (!line.isPresent()) {
            return Ret.buildFailParam("车次不存在");
        }
        List<Station> stationsByLine = stationRepository.findStationsByLine(lineName);
        List<Direction> directions = directionRepository.findByLineEquals(lineName);

        return Ret.buildSuc(
                LineInfoVo.builder().line(line.get()).direction(directions).station(stationsByLine).build()
                , "获取成功");
    }

    private final static long TTL = 1000 * 6;

    @ApiOperation(value = "获取车辆的实时信息", notes = "该接口会请求目标网站，抓取实时结果，并且缓存几秒钟；第一次请求，会触发抓取，但是不会返回结果，需要前端发两次获取结果")
    @GetMapping("bus/realTime")
    public Object getRealTimeStatus(@ApiParam(value = "车次名称", example = "专60") @RequestParam("line") String line,
                                    @ApiParam(value = "车次方向", example = "4900468341734851543") @RequestParam("direction") String direction,
                                    @ApiParam(value = "车站ID", example = "5")
                                    @RequestParam("stationId") String stationId) {
        if (!lineRepository.existsById(line)) {
            return Ret.buildFailParam("请检查【车次】");
        }

        if (!directionRepository.existsById(direction)) {
            return Ret.buildFailParam("请检查【方向】");
        }

        Optional<Station> station = stationRepository.findById(new StationId(stationId, direction, line));
        if (!station.isPresent()) {
            return Ret.buildFailParam("请检查【站点】");
        }

        //todo 自动失效的策略 不然会攒很多数据，内存占用变大。
        //todo 此处也容易有并发问题
        Optional<BusRealTime> busRealTimeOptional = busRealTimeRepository.findById(new BusRealTimeId(line, direction, stationId));
        if (!busRealTimeOptional.isPresent() || busRealTimeOptional.get().getUpdateTime() + TTL < System.currentTimeMillis()) {
            //不存在或者 超过缓存时间，就重新获取
            log.info("re pick");
            CrawlerCache.consumeRequest(BusRealTimeSrawler.buildRequest(line, direction, stationId));
        }

        busRealTimeOptional.ifPresent(busRealTime -> busRealTime.setName(station.get().getName()));

        return Ret.buildSuc(busRealTimeOptional);
    }

}
