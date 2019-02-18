package com.gdp.restgdp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
public class CountryController
{
    private final CountryRepository countryRepository;
    private final RabbitTemplate rabbitTemplate;

    public CountryController(CountryRepository countryRepository, RabbitTemplate rabbitTemplate)
    {
        this.countryRepository = countryRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @GetMapping("/names")
    public List<Country> getAllByName()
    {
        List<Country> temporaryList = new ArrayList<Country>(countryRepository.findAll());
        temporaryList.sort((c1, c2) -> c1.getCountry().compareToIgnoreCase(c2.getCountry()));

        CountryLog message = new CountryLog("Requested all by name");
        rabbitTemplate.convertAndSend(RestGdpApplication.QUEUE_NAME, message.toString());
        log.info("Message sent");

        return temporaryList;
    }

    @GetMapping("/economy")
    public List<Country> getAllByGdp()
    {
        List<Country> temporaryList = new ArrayList<Country>(countryRepository.findAll());
        temporaryList.sort((c1, c2) -> (int) (c2.getGdp() - c1.getGdp()));

        CountryLog message = new CountryLog("Requested all by GDP");
        rabbitTemplate.convertAndSend(RestGdpApplication.QUEUE_NAME, message.toString());
        log.info("Message sent");

        return temporaryList;
    }

    @GetMapping("/total")
    public ObjectNode getTotal()
    {
        List<Country> countries = countryRepository.findAll();

        Long total = 0L;
        for (Country c : countries)
        {
            total = total + c.getGdp();
        }

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode totalGdp = mapper.createObjectNode();
        totalGdp.put("country", "total");
        totalGdp.put("gdp", total);

        CountryLog message = new CountryLog("Requested total GDP");
        rabbitTemplate.convertAndSend(RestGdpApplication.QUEUE_NAME, message.toString());
        log.info("Message sent");

        return totalGdp;
    }

    @GetMapping("/gdp/{country}")
    public List<Country> getCountry(@PathVariable String country)
    {
        List<Country> temporaryList = new ArrayList<Country>(countryRepository.findAll());
        temporaryList.removeIf(c -> c.getCountry().toLowerCase().contains(country.toLowerCase()) == false);

        CountryLog message = new CountryLog("Searched for " + country);
        rabbitTemplate.convertAndSend(RestGdpApplication.QUEUE_NAME, message.toString());
        log.info("Message sent");

        if (temporaryList.isEmpty())
        {
            throw new CountryNotFoundException(country);
        }

        return temporaryList;
    }

    @PostMapping("/gdp")
    public List<Country> postGdp(@RequestBody List<Country> newCountries)
    {
        CountryLog message = new CountryLog("Posted GDP data");
        rabbitTemplate.convertAndSend(RestGdpApplication.QUEUE_NAME, message.toString());
        log.info("Message sent");

        return countryRepository.saveAll(newCountries);
    }
}
