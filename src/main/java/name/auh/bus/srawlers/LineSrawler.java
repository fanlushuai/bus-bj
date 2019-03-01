package name.auh.bus.srawlers;

import cn.wanghaomiao.seimi.annotation.Crawler;
import cn.wanghaomiao.seimi.http.SeimiHttpType;
import cn.wanghaomiao.seimi.struct.Request;
import cn.wanghaomiao.seimi.struct.Response;
import lombok.extern.slf4j.Slf4j;
import name.auh.bus.data.bean.Direction;
import name.auh.bus.data.bean.Line;
import name.auh.bus.data.bean.Station;
import name.auh.bus.data.repository.DirectionRepository;
import name.auh.bus.data.repository.LineRepository;
import name.auh.bus.data.repository.SrawlerRepository;
import name.auh.bus.data.repository.StationRepository;
import org.seimicrawler.xpath.JXNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import java.util.ArrayList;
import java.util.List;

@Crawler(name = "Line", httpType = SeimiHttpType.OK_HTTP3, useUnrepeated = false, httpTimeOut = 10000)
@Slf4j
public class LineSrawler extends BaseCrawLer implements CommandLineRunner {

    private static final String URL_LINE = "http://www.bjbus.com/home/index.php";

    private static final String XPATH_LINE = "//dd[@id='selBLine']/a/text()";

    @Autowired
    StationRepository stationRepository;

    @Autowired
    DirectionRepository directionRepository;

    @Autowired
    LineRepository lineRepository;

    @Autowired
    SrawlerRepository srawlerRepository;

    public static Request buildLineRequest() {
        return new Request(URL_LINE, "parseLine");
    }

    public void pushLine() {
        log.info("Line");
        push(buildLineRequest());
    }

    public void parseLine(Response response) {
        List<JXNode> jxNode = response.document().selN(XPATH_LINE);
        List<Line> lines = new ArrayList<>();
        for (JXNode node : jxNode) {
            log.info("Line {}", node.asString());

            Line line = new Line();
            line.setName(node.asString());
            lines.add(line);

        }
        lineRepository.saveAll(lines);
        log.info("Direction>>>");
        for (Line line : lines) {
            pushDirection(line.getName());
//            pushDirection("专60");
        }

        log.info("Direction<<<");

    }

    private static final String URL_LINE_DIRECTION = "http://www.bjbus.com/home/ajax_rtbus_data.php?act=getLineDir&selBLine=";

    private static final String XPATH_LINE_DIRECTION_XPATH = "//a";

    private static final String XPATH_LINE_DIRECTION_DIRECTION_DESC = "//a/text()";

    private static final String XPATH_LINE_DIRECTION_ID = "//a/@data-uuid";

    private void pushDirection(String line) {
        Request request = Request.build(URL_LINE_DIRECTION + line, "parseDirection");
        request.getMeta().put("line", line);
        this.push(request);
    }

    public void parseDirection(Response response) {
        String line = (String) response.getMeta().get("line");
        List<JXNode> jxNode = response.document().selN(XPATH_LINE_DIRECTION_XPATH);
        List<Direction> directions = new ArrayList<>();
        for (JXNode node : jxNode) {
            String directionId = node.sel(XPATH_LINE_DIRECTION_ID).get(0).asString();
            String directionDesc = node.sel(XPATH_LINE_DIRECTION_DIRECTION_DESC).get(0).asString();
            log.info("Direction id {}  dir {}", directionId, directionDesc);
            Direction direction = new Direction();
            direction.setDesc(directionDesc);
            direction.setId(directionId);
            direction.setLine(line);
            directions.add(direction);
        }
        directionRepository.saveAll(directions);

        log.info("Station>>>");

        for (Direction direction : directions) {
            pushStation(line, direction.getId());
        }
        log.info("Station<<<");
    }

    private static final String URL_STATION = "http://www.bjbus.com/home/ajax_rtbus_data.php?act=getDirStation&selBLine=1&selBDir=";

    private static final String XPATH_STATION = "//a";

    private static final String XPATH_STATION_ID = "//a/@data-seq";

    private static final String XPATH_STATION_NAME = "//a/text()";

    public void pushStation(String line, String direction) {
        Request request = Request.build(URL_STATION + direction, "parseStation");
        request.getMeta().put("line", line);
        request.getMeta().put("direction", direction);

        this.push(request);
    }

    public void parseStation(Response response) {
        String line = (String) response.getMeta().get("line");
        String direction = (String) response.getMeta().get("direction");
        List<JXNode> jxNode = response.document().selN(XPATH_STATION);
        List<Station> stations = new ArrayList<>();
        for (JXNode node : jxNode) {
            String id = node.sel(XPATH_STATION_ID).get(0).asString();
            String name = node.sel(XPATH_STATION_NAME).get(0).asString();
            log.info("Station id {}  name {}", id, name);
            Station station = new Station();
            station.setDir(direction);
            station.setLine(line);
            station.setId(id);
            station.setName(name);
            stations.add(station);
        }

        stationRepository.saveAll(stations);
    }

    @Override
    public void run(String... args) {
        //使用内存模式的话，可以在项目启动之后启动基础数据的抓取
//        pushLine();
    }
}
