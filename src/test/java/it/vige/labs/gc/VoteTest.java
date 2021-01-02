package it.vige.labs.gc;

import static org.keycloak.OAuth2Constants.CLIENT_CREDENTIALS;
import static org.keycloak.OAuth2Constants.GRANT_TYPE;
import static org.keycloak.adapters.KeycloakDeploymentBuilder.build;
import static org.keycloak.adapters.authentication.ClientCredentialsProviderUtils.setClientCredentials;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.adapters.spi.KeycloakAccount;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import it.vige.labs.gc.bean.VoteRequest;
import it.vige.labs.gc.bean.result.VotingPapers;
import it.vige.labs.gc.bean.vote.Candidate;
import it.vige.labs.gc.bean.vote.Group;
import it.vige.labs.gc.bean.vote.Party;
import it.vige.labs.gc.bean.vote.Vote;
import it.vige.labs.gc.bean.vote.VotingPaper;
import it.vige.labs.gc.bean.votingpapers.State;
import it.vige.labs.gc.messages.Messages;
import it.vige.labs.gc.rest.Validator;
import it.vige.labs.gc.rest.VoteController;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("dev")
public class VoteTest {

	private Logger logger = LoggerFactory.getLogger(VoteTest.class);

	@Autowired
	private VoteController voteController;

	@Autowired
	private KeycloakRestTemplate restTemplate;

	@Value("${votingpapers.scheme}")
	private String votingpapersScheme;

	@Value("${votingpapers.host}")
	private String votingpapersHost;

	@Value("${votingpapers.port}")
	private int votingpapersPort;

	private static String token;

	private static Principal principal = new Principal() {

		@Override
		public String getName() {
			return "myprincipal";
		}

	};

	private static Set<String> roles = new HashSet<String>(
			Arrays.asList(new String[] { "admin", "votaoperator", "representative", "citizen" }));

	@BeforeClass
	public static void setAuthentication() throws FileNotFoundException {
		FileInputStream config = new FileInputStream("src/test/resources/keycloak.json");
		KeycloakDeployment deployment = build(config);
		Map<String, String> reqHeaders = new HashMap<>();
		Map<String, String> reqParams = new HashMap<>();
		setClientCredentials(deployment, reqHeaders, reqParams);
		HttpHeaders headers = new HttpHeaders();
		reqHeaders.forEach((x, y) -> {
			headers.add(x, y);
		});
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add(GRANT_TYPE, CLIENT_CREDENTIALS);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		RestTemplate restTemplate = new RestTemplate();
		String url = deployment.getTokenUrl();
		ResponseEntity<AccessTokenResponse> response = restTemplate.exchange(url, HttpMethod.POST, request,
				AccessTokenResponse.class, reqParams);
		token = response.getBody().getToken();

		RefreshableKeycloakSecurityContext securityContext = new RefreshableKeycloakSecurityContext(null, null, token,
				null, null, null, null);
		KeycloakAccount account = new SimpleKeycloakAccount(principal, roles, securityContext);
		SecurityContextHolder.getContext().setAuthentication(new KeycloakAuthenticationToken(account, true));
	}

	private void setState(State state) {
		UriComponents uriComponents = UriComponentsBuilder.newInstance().scheme(votingpapersScheme)
				.host(votingpapersHost).port(votingpapersPort).path("/state?state="+state).buildAndExpand();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + token);
		HttpEntity<?> request = new HttpEntity<>(headers);
		restTemplate.exchange(uriComponents.toString(), HttpMethod.GET, request, Messages.class);
	}

	@Test
	public void voteOk() throws Exception {

		setState(State.VOTE);
		Party pd = new Party(3);
		Group michelBarbet = new Group(5);
		VotingPaper comunali = new VotingPaper(0, pd, michelBarbet);

		Candidate paolaTaverna = new Candidate(31);
		Candidate matteoCastorino = new Candidate(32);
		Party movimento5Stelle = new Party(28, Arrays.asList(new Candidate[] { paolaTaverna, matteoCastorino }));
		Group forzaItalia = new Group(12);
		VotingPaper regionali = new VotingPaper(11, movimento5Stelle, forzaItalia);

		Party noiConSalvini = new Party(94);
		Group matteoSalvini = new Group(93);
		VotingPaper nazionali = new VotingPaper(86, noiConSalvini, matteoSalvini);

		Candidate giorgiaMeloni = new Candidate(171);
		Candidate francescoAcquaroli = new Candidate(172);
		Candidate ariannaAlessandrini = new Candidate(173);
		Party fratelliDitalia = new Party(127,
				Arrays.asList(new Candidate[] { giorgiaMeloni, francescoAcquaroli, ariannaAlessandrini }));
		VotingPaper europee = new VotingPaper(121, fratelliDitalia);

		Vote vote = new Vote(Arrays.asList(new VotingPaper[] { comunali, regionali, nazionali, europee }));
		Messages messages = voteController.vote(vote);
		logger.info(messages + "");
		Assert.assertArrayEquals("the result is ok", Validator.defaultMessage.getMessages().toArray(),
				messages.getMessages().toArray());
		Assert.assertTrue(messages.isOk());

		VotingPapers votingPapers = voteController.getResult(new VoteRequest(vote, new VotingPapers())).getVotings()
				.get(0);
		Assert.assertTrue(votingPapers.getElectors() == 1);
		Assert.assertTrue(votingPapers.getMapVotingPapers().size() == 4);
		Assert.assertTrue(votingPapers.getMapVotingPapers().values().stream().allMatch(e -> e.getElectors() == 1));
		votingPapers.getMapVotingPapers().values().stream().forEach(e -> {
			Assert.assertTrue(e.getId() == 0 || e.getId() == 11 || e.getId() == 86 || e.getId() == 121);
			if (e.getId() == 0) {
				Assert.assertTrue(e.getMapGroups().values().stream().filter(f -> f.getId() == 5).findFirst().get()
						.getElectors() == 1);
				Assert.assertTrue(e.getMapGroups().values().stream()
						.flatMap(f -> Arrays
								.stream(f.getMapParties().values().toArray(new it.vige.labs.gc.bean.result.Party[0])))
						.allMatch(g -> g.getId() == 3 && g.getElectors() == 1));
				Assert.assertTrue(e.getMapGroups().values().stream()
						.flatMap(f -> Arrays
								.stream(f.getMapParties().values().toArray(new it.vige.labs.gc.bean.result.Party[0])))
						.flatMap(f -> Arrays.stream(
								f.getMapCandidates().values().toArray(new it.vige.labs.gc.bean.result.Candidate[0])))
						.count() == 0);
				Assert.assertEquals("no blank papers", 0, e.getBlankPapers());
			}
			if (e.getId() == 11) {
				Assert.assertTrue(e.getMapGroups().values().stream().filter(f -> f.getId() == 12).findFirst().get()
						.getElectors() == 1);
				Assert.assertTrue(e.getMapGroups().values().stream()
						.flatMap(f -> Arrays
								.stream(f.getMapParties().values().toArray(new it.vige.labs.gc.bean.result.Party[0])))
						.allMatch(g -> g.getId() == 28 && g.getElectors() == 1));
				Assert.assertFalse(e.getMapGroups().values().stream()
						.flatMap(f -> Arrays
								.stream(f.getMapParties().values().toArray(new it.vige.labs.gc.bean.result.Party[0])))
						.flatMap(f -> Arrays.stream(
								f.getMapCandidates().values().toArray(new it.vige.labs.gc.bean.result.Candidate[0])))
						.count() == 0);
				Assert.assertTrue(e.getMapGroups().values().stream()
						.flatMap(f -> Arrays
								.stream(f.getMapParties().values().toArray(new it.vige.labs.gc.bean.result.Party[0])))
						.flatMap(f -> Arrays.stream(
								f.getMapCandidates().values().toArray(new it.vige.labs.gc.bean.result.Candidate[0])))
						.anyMatch(f -> f.getId() == 31 || f.getId() == 32));
				Assert.assertTrue(e.getMapGroups().values().stream()
						.flatMap(f -> Arrays
								.stream(f.getMapParties().values().toArray(new it.vige.labs.gc.bean.result.Party[0])))
						.flatMap(f -> Arrays.stream(
								f.getMapCandidates().values().toArray(new it.vige.labs.gc.bean.result.Candidate[0])))
						.allMatch(f -> f.getElectors() == 1));
				Assert.assertEquals("no blank papers", 0, e.getBlankPapers());
			}
			if (e.getId() == 86) {
				Assert.assertTrue(e.getMapGroups().values().stream().filter(f -> f.getId() == 93).findFirst().get()
						.getElectors() == 1);
				Assert.assertTrue(e.getMapGroups().values().stream()
						.flatMap(f -> Arrays
								.stream(f.getMapParties().values().toArray(new it.vige.labs.gc.bean.result.Party[0])))
						.anyMatch(g -> g.getId() == 94 && g.getElectors() == 1));
				Assert.assertTrue(e.getMapGroups().values().stream()
						.flatMap(f -> Arrays
								.stream(f.getMapParties().values().toArray(new it.vige.labs.gc.bean.result.Party[0])))
						.flatMap(f -> Arrays.stream(
								f.getMapCandidates().values().toArray(new it.vige.labs.gc.bean.result.Candidate[0])))
						.count() == 0);
				Assert.assertEquals("no blank papers", 0, e.getBlankPapers());
			}
			if (e.getId() == 121) {
				Assert.assertTrue(e.getMapGroups().isEmpty());
				Assert.assertTrue(
						e.getMapParties().values().stream().allMatch(g -> g.getId() == 127 && g.getElectors() == 1));
				Assert.assertFalse(e.getMapParties().values().stream()
						.flatMap(f -> Arrays.stream(
								f.getMapCandidates().values().toArray(new it.vige.labs.gc.bean.result.Candidate[0])))
						.count() == 0);
				Assert.assertTrue(e.getMapParties().values().stream()
						.flatMap(f -> Arrays.stream(
								f.getMapCandidates().values().toArray(new it.vige.labs.gc.bean.result.Candidate[0])))
						.allMatch(f -> f.getId() == 171 || f.getId() == 172 || f.getId() == 173));
				Assert.assertTrue(e.getMapParties().values().stream()
						.flatMap(f -> Arrays.stream(
								f.getMapCandidates().values().toArray(new it.vige.labs.gc.bean.result.Candidate[0])))
						.allMatch(f -> f.getElectors() == 1));
				Assert.assertEquals("no blank papers", 0, e.getBlankPapers());
			}
		});
		setState(State.PREPARE);
	}

	@Test
	public void onlySelection() throws Exception {

		setState(State.VOTE);
		Group matteoSalvini = new Group(95);
		VotingPaper nazionali = new VotingPaper(86, null, matteoSalvini);
		VotingPaper comunali = new VotingPaper(0);
		VotingPaper regionali = new VotingPaper(11);
		VotingPaper europee = new VotingPaper(121);

		Vote vote = new Vote(new ArrayList<VotingPaper>(Arrays.asList(new VotingPaper[] { nazionali })));
		Messages messages = voteController.vote(vote);
		logger.info(messages + "");
		Assert.assertFalse("we need to send all the voting papers", messages.isOk());

		vote.getVotingPapers().add(comunali);
		vote.getVotingPapers().add(regionali);
		vote.getVotingPapers().add(europee);
		messages = voteController.vote(vote);
		logger.info(messages + "");
		Assert.assertArrayEquals("only a group without party is ok", Validator.defaultMessage.getMessages().toArray(),
				messages.getMessages().toArray());
		Assert.assertTrue(messages.isOk());

		VotingPapers votingPapers = voteController.getResult(new VoteRequest(vote, new VotingPapers())).getVotings()
				.get(0);
		Assert.assertTrue(votingPapers.getElectors() == 1);
		Assert.assertTrue(votingPapers.getMapVotingPapers().size() == 4);
		Assert.assertTrue(votingPapers.getMapVotingPapers().values().stream().allMatch(e -> e.getElectors() == 1));
		Assert.assertTrue(votingPapers.getMapVotingPapers().values().stream()
				.allMatch(e -> e.getId() == 0 || e.getId() == 86 || e.getId() == 11 || e.getId() == 121));
		votingPapers.getMapVotingPapers().values().stream().forEach(e -> {
			if (e.getId() == 86) {
				Assert.assertTrue(e.getMapGroups().values().stream().filter(f -> f.getId() == 95).findFirst().get()
						.getElectors() == 1);
				Assert.assertTrue(e.getMapGroups().values().stream()
						.flatMap(f -> Arrays
								.stream(f.getMapParties().values().toArray(new it.vige.labs.gc.bean.result.Party[0])))
						.count() == 0);
				Assert.assertEquals("no blank papers", 0, e.getBlankPapers());
			}
		});

		nazionali.setGroup(null);
		messages = voteController.vote(vote);
		logger.info(messages + "");
		Assert.assertArrayEquals("no group and no party is a blank paper",
				Validator.defaultMessage.getMessages().toArray(), messages.getMessages().toArray());
		Assert.assertTrue(messages.isOk());

		Party fratelliDItalia = new Party(96);
		nazionali.setParty(fratelliDItalia);
		messages = voteController.vote(vote);
		logger.info(messages + "");
		Assert.assertArrayEquals("a party without group is not ok if not disjointed",
				Validator.errorMessage.getMessages().toArray(), messages.getMessages().toArray());
		Assert.assertFalse(messages.isOk());

		Group michelBarbet = new Group(5);
		comunali.setGroup(michelBarbet);
		vote = new Vote(new ArrayList<VotingPaper>(Arrays.asList(new VotingPaper[] { comunali })));
		vote.getVotingPapers().add(new VotingPaper(86));
		vote.getVotingPapers().add(regionali);
		vote.getVotingPapers().add(europee);
		messages = voteController.vote(vote);
		logger.info(messages + "");
		Assert.assertArrayEquals("only a group without party is ok", Validator.defaultMessage.getMessages().toArray(),
				messages.getMessages().toArray());
		Assert.assertTrue(messages.isOk());

		votingPapers = voteController.getResult(new VoteRequest(vote, new VotingPapers())).getVotings().get(0);
		Assert.assertTrue(votingPapers.getElectors() == 1);
		Assert.assertTrue(votingPapers.getMapVotingPapers().size() == 4);
		Assert.assertTrue(votingPapers.getMapVotingPapers().values().stream().allMatch(e -> e.getElectors() == 1));
		Assert.assertTrue(votingPapers.getMapVotingPapers().values().stream()
				.allMatch(e -> e.getId() == 0 || e.getId() == 86 || e.getId() == 11 || e.getId() == 121));
		votingPapers.getMapVotingPapers().values().stream().forEach(e -> {
			if (e.getId() == 0) {
				Assert.assertTrue(e.getMapGroups().values().stream().filter(f -> f.getId() == 5).findFirst().get()
						.getElectors() == 1);
				Assert.assertTrue(e.getMapGroups().values().stream()
						.flatMap(f -> Arrays
								.stream(f.getMapParties().values().toArray(new it.vige.labs.gc.bean.result.Party[0])))
						.count() == 0);
				Assert.assertEquals("no blank papers", 0, e.getBlankPapers());
			}
		});

		comunali.setGroup(null);
		messages = voteController.vote(vote);
		logger.info(messages + "");
		Assert.assertArrayEquals("no group and no party is a blank paper",
				Validator.defaultMessage.getMessages().toArray(), messages.getMessages().toArray());
		Assert.assertTrue(messages.isOk());

		Party pd = new Party(3);
		comunali.setParty(pd);
		messages = voteController.vote(vote);
		logger.info(messages + "");
		Assert.assertArrayEquals("a party without group is ok if disjointed",
				Validator.defaultMessage.getMessages().toArray(), messages.getMessages().toArray());
		Assert.assertTrue(messages.isOk());

		votingPapers = voteController.getResult(new VoteRequest(vote, new VotingPapers())).getVotings().get(0);
		Assert.assertTrue(votingPapers.getElectors() == 1);
		Assert.assertTrue(votingPapers.getMapVotingPapers().size() == 4);
		Assert.assertTrue(votingPapers.getMapVotingPapers().values().stream().allMatch(e -> e.getElectors() == 1));
		Assert.assertTrue(votingPapers.getMapVotingPapers().values().stream()
				.allMatch(e -> e.getId() == 0 || e.getId() == 86 || e.getId() == 11 || e.getId() == 121));
		votingPapers.getMapVotingPapers().values().stream().forEach(e -> {
			if (e.getId() == 0) {
				Assert.assertTrue(e.getMapGroups().isEmpty());
				Assert.assertTrue(e.getMapParties().values().stream().filter(f -> f.getId() == 3).findFirst().get()
						.getElectors() == 1);
				Assert.assertFalse(e.getMapParties().values().stream()
						.flatMap(f -> Arrays.stream(
								f.getMapCandidates().values().toArray(new it.vige.labs.gc.bean.result.Candidate[0])))
						.count() == 1);
				Assert.assertTrue(e.getMapParties().values().stream()
						.flatMap(f -> Arrays.stream(
								f.getMapCandidates().values().toArray(new it.vige.labs.gc.bean.result.Candidate[0])))
						.allMatch(f -> f.getId() == 171 || f.getId() == 172 || f.getId() == 173));
				Assert.assertTrue(e.getMapParties().values().stream()
						.flatMap(f -> Arrays.stream(
								f.getMapCandidates().values().toArray(new it.vige.labs.gc.bean.result.Candidate[0])))
						.allMatch(f -> f.getElectors() == 1));
				Assert.assertEquals("no blank papers", 0, e.getBlankPapers());
			}
		});
		setState(State.PREPARE);
	}

	@Test
	public void ids() throws Exception {

		setState(State.VOTE);
		Party giorgiaMeloni = new Party(93);
		Group matteoSalvini = new Group(95);
		VotingPaper nazionali = new VotingPaper(86, giorgiaMeloni, matteoSalvini);

		Vote vote = new Vote(Arrays.asList(new VotingPaper[] { nazionali }));
		Messages messages = voteController.vote(vote);
		logger.info(messages + "");
		Assert.assertArrayEquals("the ids are inverted", Validator.errorMessage.getMessages().toArray(),
				messages.getMessages().toArray());
		Assert.assertFalse(messages.isOk());

		Party noiConSalvini = new Party(94);
		matteoSalvini.setId(1122);
		nazionali.setParty(noiConSalvini);
		messages = voteController.vote(vote);
		logger.info(messages + "");
		Assert.assertArrayEquals("the id doesn't exist", Validator.errorMessage.getMessages().toArray(),
				messages.getMessages().toArray());
		Assert.assertFalse(messages.isOk());
		setState(State.PREPARE);
	}

	@Test
	public void disjointed() throws Exception {

		setState(State.VOTE);
		Party giorgiaMeloni = new Party(95);
		Group matteoSalvini = new Group(93);
		VotingPaper nazionali = new VotingPaper(86, giorgiaMeloni, matteoSalvini);

		Vote vote = new Vote(Arrays.asList(new VotingPaper[] { nazionali }));
		Messages messages = voteController.vote(vote);
		logger.info(messages + "");
		Assert.assertArrayEquals("the vote is not disjointed, you can select only the group of the party",
				Validator.errorMessage.getMessages().toArray(), messages.getMessages().toArray());
		Assert.assertFalse(messages.isOk());
		setState(State.PREPARE);
	}

	@Test
	public void candidates() throws Exception {

		setState(State.VOTE);
		VotingPaper comunali = new VotingPaper(0);
		VotingPaper regionali = new VotingPaper(11);
		VotingPaper nazionali = new VotingPaper(86);
		VotingPaper europee = new VotingPaper(121);

		Candidate giulianoSantoboni = new Candidate(30);
		Candidate matteoCastorino = new Candidate(32);
		Party movimento5Stelle = new Party(28,
				new ArrayList<Candidate>(Arrays.asList(new Candidate[] { giulianoSantoboni, matteoCastorino })));
		Group forzaItalia = new Group(12);
		regionali.setGroup(forzaItalia);
		regionali.setParty(movimento5Stelle);

		Vote vote = new Vote(new ArrayList<VotingPaper>(Arrays.asList(new VotingPaper[] { regionali })));
		Messages messages = voteController.vote(vote);
		logger.info(messages + "");
		Assert.assertArrayEquals("we need to send all the voting papers",
				Validator.errorMessage.getMessages().toArray(), messages.getMessages().toArray());
		Assert.assertFalse(messages.isOk());

		vote.getVotingPapers().add(comunali);
		vote.getVotingPapers().add(nazionali);
		vote.getVotingPapers().add(europee);
		messages = voteController.vote(vote);
		logger.info(messages + "");
		Assert.assertArrayEquals("there are two M and no F", Validator.errorMessage.getMessages().toArray(),
				messages.getMessages().toArray());
		Assert.assertFalse(messages.isOk());

		movimento5Stelle.getCandidates().remove(0);
		messages = voteController.vote(vote);
		logger.info(messages + "");
		Assert.assertArrayEquals("there is only a candidate, it's ok", Validator.defaultMessage.getMessages().toArray(),
				messages.getMessages().toArray());
		Assert.assertTrue(messages.isOk());

		VotingPapers votingPapers = voteController.getResult(new VoteRequest(vote, new VotingPapers())).getVotings()
				.get(0);
		Assert.assertTrue(votingPapers.getElectors() == 1);
		Assert.assertTrue(votingPapers.getMapVotingPapers().size() == 4);
		Assert.assertTrue(votingPapers.getMapVotingPapers().values().stream().allMatch(e -> e.getElectors() == 1));
		Assert.assertTrue(votingPapers.getMapVotingPapers().values().stream()
				.allMatch(e -> e.getId() == 0 || e.getId() == 86 || e.getId() == 11 || e.getId() == 121));
		votingPapers.getMapVotingPapers().values().stream().forEach(e -> {
			if (e.getId() == 11) {
				Assert.assertTrue(e.getMapGroups().values().stream().filter(f -> f.getId() == 12).findFirst().get()
						.getElectors() == 1);
				Assert.assertTrue(e.getMapGroups().values().stream()
						.flatMap(f -> Arrays
								.stream(f.getMapParties().values().toArray(new it.vige.labs.gc.bean.result.Party[0])))
						.allMatch(g -> g.getId() == 28 && g.getElectors() == 1));
				Assert.assertFalse(e.getMapGroups().values().stream()
						.flatMap(f -> Arrays
								.stream(f.getMapParties().values().toArray(new it.vige.labs.gc.bean.result.Party[0])))
						.flatMap(f -> Arrays.stream(
								f.getMapCandidates().values().toArray(new it.vige.labs.gc.bean.result.Candidate[0])))
						.count() == 0);
				Assert.assertTrue(e.getMapGroups().values().stream()
						.flatMap(f -> Arrays
								.stream(f.getMapParties().values().toArray(new it.vige.labs.gc.bean.result.Party[0])))
						.flatMap(f -> Arrays.stream(
								f.getMapCandidates().values().toArray(new it.vige.labs.gc.bean.result.Candidate[0])))
						.allMatch(f -> f.getId() == 32));
				Assert.assertTrue(e.getMapGroups().values().stream()
						.flatMap(f -> Arrays
								.stream(f.getMapParties().values().toArray(new it.vige.labs.gc.bean.result.Party[0])))
						.flatMap(f -> Arrays.stream(
								f.getMapCandidates().values().toArray(new it.vige.labs.gc.bean.result.Candidate[0])))
						.allMatch(f -> f.getElectors() == 1));
				Assert.assertEquals("no blank papers", 0, e.getBlankPapers());
			}
		});

		Candidate paolaTaverna = new Candidate(31);
		movimento5Stelle.getCandidates().add(giulianoSantoboni);
		movimento5Stelle.getCandidates().add(paolaTaverna);
		messages = voteController.vote(vote);
		logger.info(messages + "");
		Assert.assertArrayEquals("candidates number exceed the max candidates value",
				Validator.errorMessage.getMessages().toArray(), messages.getMessages().toArray());
		Assert.assertFalse(messages.isOk());

		movimento5Stelle.getCandidates().clear();
		messages = voteController.vote(vote);
		logger.info(messages + "");
		Assert.assertArrayEquals("no candidates, the result is ok", Validator.defaultMessage.getMessages().toArray(),
				messages.getMessages().toArray());
		Assert.assertTrue(messages.isOk());
		setState(State.PREPARE);
	}

}
