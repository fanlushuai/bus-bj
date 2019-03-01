package name.auh.bus.service;

import name.auh.bus.data.repository.LineRepository;
import name.auh.bus.data.repository.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BusService {

    @Autowired
    StationRepository stationRepository;

    @Autowired
    LineRepository lineRepository;


}
