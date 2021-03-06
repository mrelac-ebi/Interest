package mousephenotype.interest;

import mousephenotype.config.TestConfig;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by mrelac on 02/05/2017.
 */
@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = TestConfig.class)
@TestPropertySource("file:${user.home}/configfiles/${profile:dev}/ri.test.properties")
public class InterestControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;


    @Before
    public void setUp() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @After
    public void tearDown() throws Exception {
    }


    // Test for malformed / nonexisting email

    @Test
    public void queryEmailWithMalformedEmail() throws Exception {

        String url = "/email/junk";

        this.mockMvc.perform(
                get(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void queryEmailWithMalformedEmailAndNonexistentGene() throws Exception {

        String url = "/email/junk?mgiAccessionId=junk";

        this.mockMvc.perform(
                get(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().is4xxClientError());

//        RestTemplate rt = new RestTemplate();
//        List<Interest> actualResults = new ArrayList<>();
//        ParameterizedTypeReference<List<Interest>> typeRef = new ParameterizedTypeReference<List<Interest>>() { };
//        ResponseEntity<List<Interest>> responseEntity;
//
//        try {
//            responseEntity = rt.exchange(url, HttpMethod.GET, new HttpEntity<>(actualResults), typeRef);
//            assertTrue("Expected HttpStatus.NOT_FOUND but got " + responseEntity.getStatusCode().toString(), responseEntity.getStatusCode().equals(HttpStatus.NOT_FOUND));
//
//        } catch (HttpClientErrorException e) {
//            assertTrue("Expected HttpStatus.NOT_FOUND but got " + e.getStatusCode().toString(), e.getStatusCode().equals(HttpStatus.NOT_FOUND));
//        }
    }

    @Test
    public void queryEmailWithNonexistingEmail() throws Exception {

        String url = "/email/junk@ebi.ac.uk";

        this.mockMvc.perform(
                get(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void queryEmailWithNonexistingEmailAndGene() throws Exception {

        String url = "/email/junk@ebi.ac.uk?mgiAccessionId=MGI:0000010";

        this.mockMvc.perform(
                get(url)
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().is4xxClientError());
    }


    // Test for existing email, nonexisting gene


    @Test
    public void queryEmailWithEmailAndNonexistentGene() throws Exception {

        String url = "/email/mrelac@ebi.ac.uk?mgiAccessionId=junk";

        this.mockMvc.perform(
                get(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().is4xxClientError());
    }


    // Test for existing email, existing gene(s)


    @Test
    public void queryEmailWithEmailExpectMultipleGenes() throws Exception {

        String url = "/email/mrelac@ebi.ac.uk";

        this.mockMvc.perform(
                get(url)
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].address", Matchers.comparesEqualTo("mrelac@ebi.ac.uk")))
                .andExpect(jsonPath("$[0].mgiAccessionId", Matchers.comparesEqualTo("MGI:0000010")))
                .andExpect(jsonPath("$[0].active", Matchers.comparesEqualTo(true)))

                .andExpect(jsonPath("$[1].address", Matchers.comparesEqualTo("mrelac@ebi.ac.uk")))
                .andExpect(jsonPath("$[1].mgiAccessionId", Matchers.comparesEqualTo("MGI:0000030")))
                .andExpect(jsonPath("$[1].active", Matchers.comparesEqualTo(true)))

                .andExpect(jsonPath("$[2].address", Matchers.comparesEqualTo("mrelac@ebi.ac.uk")))
                .andExpect(jsonPath("$[2].mgiAccessionId", Matchers.comparesEqualTo("MGI:0000020")))
                .andExpect(jsonPath("$[2].active", Matchers.comparesEqualTo(true)))
        ;
    }

    @Test
    public void queryEmailWithEmailAndGeneExpectSingleGene() throws Exception {

        String url = "/email/mrelac@ebi.ac.uk?mgiAccessionId=MGI:0000030";

        this.mockMvc.perform(
                get(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].address", Matchers.comparesEqualTo("mrelac@ebi.ac.uk")))
                .andExpect(jsonPath("$[0].mgiAccessionId", Matchers.comparesEqualTo("MGI:0000030")))
                .andExpect(jsonPath("$[0].active", Matchers.comparesEqualTo(true)))
                ;
    }


    // Test for unknown gene


    @Test
    public void queryGeneWithNonexistingGene() throws Exception {

        String url = "/gene/MGI:junk";

        this.mockMvc.perform(
                get(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void queryGeneWithNonexistingGeneAndEmail() throws Exception {

        String url = "/gene/junk?emailAddress=mrelac@ebi.ac.uk";

        this.mockMvc.perform(
                get(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().is4xxClientError());
    }


    // Test for existing gene malformed / nonexistent email


    @Test
    public void queryGeneWithGeneAndMalformedEmail() throws Exception {

        String url = "/gene/MGI:0000020?emailAddress=junk";

        this.mockMvc.perform(
                get(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void queryGeneWithGeneAndNonexistingEmail() throws Exception {

        String url = "/gene/MGI:0000010?emailAddress=junk@ebi.ac.uk";

        this.mockMvc.perform(
                get(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().is4xxClientError());
    }


    // Test for existing gene, existing email(s)


    @Test
    public void queryGeneWithGeneExpectMultipleEmails() throws Exception {

        String url = "/gene/MGI:0000010";

        this.mockMvc.perform(
                get(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].address", Matchers.comparesEqualTo("mrelac@ebi.ac.uk")))
                .andExpect(jsonPath("$[0].mgiAccessionId", Matchers.comparesEqualTo("MGI:0000010")))
                .andExpect(jsonPath("$[0].active", Matchers.comparesEqualTo(true)))

                .andExpect(jsonPath("$[1].address", Matchers.comparesEqualTo("tmeehan@ebi.ac.uk")))
                .andExpect(jsonPath("$[1].mgiAccessionId", Matchers.comparesEqualTo("MGI:0000010")))
                .andExpect(jsonPath("$[1].active", Matchers.comparesEqualTo(true)))

                .andExpect(jsonPath("$[2].address", Matchers.comparesEqualTo("jmason@ebi.ac.uk")))
                .andExpect(jsonPath("$[2].mgiAccessionId", Matchers.comparesEqualTo("MGI:0000010")))
                .andExpect(jsonPath("$[2].active", Matchers.comparesEqualTo(true)))
        ;
    }

    @Test
    public void queryGeneWithGeneExpectSingleEmail() throws Exception {

        String url = "/gene/MGI:0000030";

        this.mockMvc.perform(
                get(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].address", Matchers.comparesEqualTo("mrelac@ebi.ac.uk")))
                .andExpect(jsonPath("$[0].mgiAccessionId", Matchers.comparesEqualTo("MGI:0000030")))
                .andExpect(jsonPath("$[0].active", Matchers.comparesEqualTo(true)))
        ;
    }

    @Test
    public void queryGeneWithGeneAndEmailExpectMrelac() throws Exception {

        String url = "/gene/MGI:0000020?emailAddress=mrelac@ebi.ac.uk";

        this.mockMvc.perform(
                get(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].address", Matchers.comparesEqualTo("mrelac@ebi.ac.uk")))
                .andExpect(jsonPath("$[0].mgiAccessionId", Matchers.comparesEqualTo("MGI:0000020")))
                .andExpect(jsonPath("$[0].active", Matchers.comparesEqualTo(true)))
        ;
    }

    @Test
    public void queryGeneWithGeneAndEmailExpectJmason() throws Exception {

        String url = "/gene/MGI:0000010?emailAddress=jmason@ebi.ac.uk";

        this.mockMvc.perform(
                get(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].address", Matchers.comparesEqualTo("jmason@ebi.ac.uk")))
                .andExpect(jsonPath("$[0].mgiAccessionId", Matchers.comparesEqualTo("MGI:0000010")))
                .andExpect(jsonPath("$[0].active", Matchers.comparesEqualTo(true)))
                ;
    }


    // REGISTER (POST)


    @Test
    public void registerEmailOnly() throws Exception {

        String url = "/mrelac@ebi.ac.uk";

        this.mockMvc.perform(
                post(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void registerGeneOnly() throws Exception {

        String url = "/MGI:0000050";

        this.mockMvc.perform(
                post(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void registerMalformedEmailAndGene() throws Exception {

        String url = "/junk/MGI:0000060";

        this.mockMvc.perform(
                post(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void registerExistingEmailAndNonexistingGene() throws Exception {

        String url = "/mrelac@ebi.ac.uk/junk";

        this.mockMvc.perform(
                post(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void registerNonexistingEmailAndNonexistingGene() throws Exception {

        String url = "/newuser@ebi.ac.uk/junk";

        this.mockMvc.perform(
                post(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().is4xxClientError());
    }


    // THESE SHOULD RETURN ROWS AND HAVE STATUS 'OK'.


    @Test
    public void registerExistingEmailAndExistingRegisteredGene() throws Exception {

        String url = "/mrelac@ebi.ac.uk/MGI:0000020";

        this.mockMvc.perform(
                post(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address", Matchers.comparesEqualTo("mrelac@ebi.ac.uk")))
                .andExpect(jsonPath("$.mgiAccessionId", Matchers.comparesEqualTo("MGI:0000020")))
                .andExpect(jsonPath("$.active", Matchers.comparesEqualTo(true)))
                ;
    }

    @Test
    public void registerNonexistingEmailAndExistingGene() throws Exception {

        String url = "/newuser2@ebi.ac.uk/MGI:0000020";

        this.mockMvc.perform(
                post(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address", Matchers.comparesEqualTo("newuser2@ebi.ac.uk")))
                .andExpect(jsonPath("$.mgiAccessionId", Matchers.comparesEqualTo("MGI:0000020")))
                .andExpect(jsonPath("$.active", Matchers.comparesEqualTo(true)))
        ;
    }

    @Test
    public void registerExistingEmailAndExistingUnregisteredGene() throws Exception {

        String url = "/jmason@ebi.ac.uk/MGI:0000020";

        this.mockMvc.perform(
                post(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address", Matchers.comparesEqualTo("jmason@ebi.ac.uk")))
                .andExpect(jsonPath("$.mgiAccessionId", Matchers.comparesEqualTo("MGI:0000020")))
                .andExpect(jsonPath("$.active", Matchers.comparesEqualTo(true)))
        ;
    }


    // UNREGISTER (DELETE)


    @Test
    public void unregisterOmitGeneComponent() throws Exception {

        String url = "/mrelac@ebi.ac.uk";

        this.mockMvc.perform(
                delete(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void unregisterMalformedEmailAndGene() throws Exception {

        String url = "/junk/MGI:0000060";

        this.mockMvc.perform(
                delete(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void unregisterExistingEmailAndNonexistingGene() throws Exception {

        String url = "/mrelac@ebi.ac.uk/junk";

        this.mockMvc.perform(
                delete(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void unregisterNonexistingEmailAndExistingGene() throws Exception {

        String url = "/junk@ebi.ac.uk/MGI:0000020";

        this.mockMvc.perform(
                delete(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void unregisterExistingEmailAndExistingUnregisteredGene() throws Exception {

        String url = "/mrelac@ebi.ac.uk/MGI:0000060";

        this.mockMvc.perform(
                delete(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void unregisterExistingEmailAndExistingRegisteredGene() throws Exception {

        String url = "/mrelac@ebi.ac.uk/MGI:0000020";

        this.mockMvc.perform(
                delete(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address", Matchers.comparesEqualTo("mrelac@ebi.ac.uk")))
                .andExpect(jsonPath("$.mgiAccessionId", Matchers.comparesEqualTo("MGI:0000020")))
                .andExpect(jsonPath("$.active", Matchers.comparesEqualTo(true)))
        ;
    }
}