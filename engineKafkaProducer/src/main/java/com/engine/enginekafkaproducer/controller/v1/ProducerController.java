package com.engine.enginekafkaproducer.controller.v1;

import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@RequestMapping("v1")
public class ProducerController {
    private final KafkaTemplate<String, String> kafkaTemplateFsBd;
    private final KafkaTemplate<String, String> kafkaTemplateFnNs;
    private final KafkaTemplate<String, String> kafkaTemplateHf;

    public ProducerController(KafkaTemplate<String, String> kafkaTemplateFsBd, KafkaTemplate<String, String> kafkaTemplateFnNs, KafkaTemplate<String, String> kafkaTemplateHf) {
        this.kafkaTemplateFsBd = kafkaTemplateFsBd;
        this.kafkaTemplateFnNs = kafkaTemplateFnNs;
        this.kafkaTemplateHf = kafkaTemplateHf;
    }

    @PostMapping(path = "/{topic}")
    public ResponseEntity<?> ProducerRequest(@RequestBody String jsonString, @PathVariable("topic") String topic) {
        System.out.println(topic);
        AtomicBoolean error = new AtomicBoolean(false);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        var future = KafkaTemplateType(topic, jsonString);

        if(future==null)
            return null;

        future.whenComplete((result, ex) -> {
            error.set(ex != null);
        });

        if (!error.get()) {
            return new ResponseEntity<>("", headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    private @Nullable CompletableFuture<SendResult<String, String>> KafkaTemplateType(String topic, String jsonString) {

//        if (FsBd.matches(topic)) {
//            return kafkaTemplateFsBd.send(topic, jsonString);
//        } else if (Hf.matches(topic)) {
//            return kafkaTemplateHf.send(topic, jsonString);
//
//        } else if (FnNs.matches(topic)) {
//            return kafkaTemplateFnNs.send(topic, jsonString);
//        } else {
//            //todo 없는 토픽일 때 처리.
//            return null;
//        }

        switch (topic) {
            case "Stocks":
            case "Disclosure":
            case "Bond":
            case "Report":
            case "CheckReport":
                return kafkaTemplateFsBd.send(topic, jsonString);
            case "Price":
            case "PriceYF":
            case "IndexComposition":
            case "IndexPrice":
            case "Indices":
                return kafkaTemplateHf.send(topic, jsonString);
            case "News":
            case "Footnote":
                return kafkaTemplateFnNs.send(topic, jsonString);
            default:
                //todo 없는 토픽일 때 처리.
                return null;
        }
    }
}
