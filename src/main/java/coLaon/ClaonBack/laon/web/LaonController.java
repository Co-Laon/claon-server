package coLaon.ClaonBack.laon.web;

import coLaon.ClaonBack.laon.Service.LaonService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/laon")
public class LaonController {
    private LaonService laonService;
}
