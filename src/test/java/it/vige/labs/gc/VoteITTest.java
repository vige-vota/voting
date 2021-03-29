package it.vige.labs.gc;

import static it.vige.labs.gc.SecurityConfig.ADMIN_ROLE;
import static it.vige.labs.gc.SecurityConfig.CITIZEN_ROLE;
import static it.vige.labs.gc.bean.votingpapers.State.PREPARE;
import static it.vige.labs.gc.bean.votingpapers.State.VOTE;
import static it.vige.labs.gc.rest.Validator.defaultMessage;
import static it.vige.labs.gc.rest.Validator.errorMessage;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.keycloak.OAuth2Constants.CLIENT_CREDENTIALS;
import static org.keycloak.OAuth2Constants.GRANT_TYPE;
import static org.keycloak.adapters.KeycloakDeploymentBuilder.build;
import static org.keycloak.adapters.authentication.ClientCredentialsProviderUtils.setClientCredentials;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;
import static org.springframework.web.util.UriComponentsBuilder.newInstance;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.adapters.spi.KeycloakAccount;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessTokenResponse;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;

import it.vige.labs.gc.bean.VoteRequest;
import it.vige.labs.gc.bean.result.VotingPapers;
import it.vige.labs.gc.bean.vote.Candidate;
import it.vige.labs.gc.bean.vote.Group;
import it.vige.labs.gc.bean.vote.Party;
import it.vige.labs.gc.bean.vote.Vote;
import it.vige.labs.gc.bean.vote.VotingPaper;
import it.vige.labs.gc.bean.votingpapers.State;
import it.vige.labs.gc.messages.Messages;
import it.vige.labs.gc.rest.VoteController;

@SpringBootTest(webEnvironment = DEFINED_PORT)
@ActiveProfiles("dev")
public class VoteITTest {

	private Logger logger = getLogger(VoteITTest.class);

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

	private static Set<String> roles = new HashSet<String>(asList(new String[] { ADMIN_ROLE, CITIZEN_ROLE }));

	@BeforeAll
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
		ResponseEntity<AccessTokenResponse> response = restTemplate.exchange(url, POST, request,
				AccessTokenResponse.class, reqParams);
		token = response.getBody().getToken();

		RefreshableKeycloakSecurityContext securityContext = new RefreshableKeycloakSecurityContext(null, null, token,
				null, null, null, null);
		KeycloakAccount account = new SimpleKeycloakAccount(principal, roles, securityContext);
		getContext().setAuthentication(new KeycloakAuthenticationToken(account, true));
	}

	private void setState(State state) {
		UriComponents uriComponents = newInstance().scheme(votingpapersScheme).host(votingpapersHost)
				.port(votingpapersPort).path("/state?state=" + state).buildAndExpand();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + token);
		HttpEntity<?> request = new HttpEntity<>(headers);
		restTemplate.exchange(uriComponents.toString(), GET, request, Messages.class);
	}

	@Test
	public void voteOk() throws Exception {

		setState(VOTE);
		Party pd = new Party(3);
		Group michelBarbet = new Group(5);
		VotingPaper comunali = new VotingPaper(0, pd, michelBarbet);

		Candidate paolaTaverna = new Candidate(31);
		Candidate matteoCastorino = new Candidate(32);
		Party movimento5Stelle = new Party(28, asList(new Candidate[] { paolaTaverna, matteoCastorino }));
		Group forzaItalia = new Group(12);
		VotingPaper regionali = new VotingPaper(11, movimento5Stelle, forzaItalia);

		Party noiConSalvini = new Party(94);
		Group matteoSalvini = new Group(93);
		VotingPaper nazionali = new VotingPaper(86, noiConSalvini, matteoSalvini);

		Candidate giorgiaMeloni = new Candidate(171);
		Candidate francescoAcquaroli = new Candidate(172);
		Candidate ariannaAlessandrini = new Candidate(173);
		Party fratelliDitalia = new Party(127,
				asList(new Candidate[] { giorgiaMeloni, francescoAcquaroli, ariannaAlessandrini }));
		VotingPaper europee = new VotingPaper(121, fratelliDitalia);

		Vote vote = new Vote(asList(new VotingPaper[] { comunali, regionali, nazionali, europee }));
		Messages messages = voteController.vote(vote);
		logger.info(messages + "");
		assertArrayEquals(defaultMessage.getMessages().toArray(), messages.getMessages().toArray(), "the result is ok");
		assertTrue(messages.isOk());

		VotingPapers votingPapers = voteController.getResult(new VoteRequest(vote, new VotingPapers())).getVotings()
				.get(0);
		assertTrue(votingPapers.getElectors() == 1);
		assertTrue(votingPapers.getMapVotingPapers().size() == 4);
		assertTrue(votingPapers.getMapVotingPapers().values().stream().allMatch(e -> e.getElectors() == 1));
		votingPapers.getMapVotingPapers().values().stream().forEach(e -> {
			assertTrue(e.getId() == 0 || e.getId() == 11 || e.getId() == 86 || e.getId() == 121);
			if (e.getId() == 0) {
				assertTrue(e.getMapGroups().values().stream().filter(f -> f.getId() == 5).findFirst().get()
						.getElectors() == 1);
				assertTrue(e.getMapGroups().values().stream().flatMap(
						f -> stream(f.getMapParties().values().toArray(new it.vige.labs.gc.bean.result.Party[0])))
						.allMatch(g -> g.getId() == 3 && g.getElectors() == 1));
				assertTrue(e.getMapGroups().values().stream().flatMap(
						f -> stream(f.getMapParties().values().toArray(new it.vige.labs.gc.bean.result.Party[0])))
						.flatMap(f -> stream(
								f.getMapCandidates().values().toArray(new it.vige.labs.gc.bean.result.Candidate[0])))
						.count() == 0);
				assertEquals(0, e.getBlankPapers(), "no blank papers");
			}
			if (e.getId() == 11) {
				assertTrue(e.getMapGroups().values().stream().filter(f -> f.getId() == 12).findFirst().get()
						.getElectors() == 1);
				assertTrue(e.getMapGroups().values().stream().flatMap(
						f -> stream(f.getMapParties().values().toArray(new it.vige.labs.gc.bean.result.Party[0])))
						.allMatch(g -> g.getId() == 28 && g.getElectors() == 1));
				assertFalse(e.getMapGroups().values().stream().flatMap(
						f -> stream(f.getMapParties().values().toArray(new it.vige.labs.gc.bean.result.Party[0])))
						.flatMap(f -> stream(
								f.getMapCandidates().values().toArray(new it.vige.labs.gc.bean.result.Candidate[0])))
						.count() == 0);
				assertTrue(e.getMapGroups().values().stream().flatMap(
						f -> stream(f.getMapParties().values().toArray(new it.vige.labs.gc.bean.result.Party[0])))
						.flatMap(f -> stream(
								f.getMapCandidates().values().toArray(new it.vige.labs.gc.bean.result.Candidate[0])))
						.anyMatch(f -> f.getId() == 31 || f.getId() == 32));
				assertTrue(e.getMapGroups().values().stream().flatMap(
						f -> stream(f.getMapParties().values().toArray(new it.vige.labs.gc.bean.result.Party[0])))
						.flatMap(f -> stream(
								f.getMapCandidates().values().toArray(new it.vige.labs.gc.bean.result.Candidate[0])))
						.allMatch(f -> f.getElectors() == 1));
				assertEquals(0, e.getBlankPapers(), "no blank papers");
			}
			if (e.getId() == 86) {
				assertTrue(e.getMapGroups().values().stream().filter(f -> f.getId() == 93).findFirst().get()
						.getElectors() == 1);
				assertTrue(e.getMapGroups().values().stream().flatMap(
						f -> stream(f.getMapParties().values().toArray(new it.vige.labs.gc.bean.result.Party[0])))
						.anyMatch(g -> g.getId() == 94 && g.getElectors() == 1));
				assertTrue(e.getMapGroups().values().stream().flatMap(
						f -> stream(f.getMapParties().values().toArray(new it.vige.labs.gc.bean.result.Party[0])))
						.flatMap(f -> stream(
								f.getMapCandidates().values().toArray(new it.vige.labs.gc.bean.result.Candidate[0])))
						.count() == 0);
				assertEquals(0, e.getBlankPapers(), "no blank papers");
			}
			if (e.getId() == 121) {
				assertTrue(e.getMapGroups().isEmpty());
				assertTrue(e.getMapParties().values().stream().allMatch(g -> g.getId() == 127 && g.getElectors() == 1));
				assertFalse(e.getMapParties().values().stream()
						.flatMap(f -> stream(
								f.getMapCandidates().values().toArray(new it.vige.labs.gc.bean.result.Candidate[0])))
						.count() == 0);
				assertTrue(e.getMapParties().values().stream()
						.flatMap(f -> stream(
								f.getMapCandidates().values().toArray(new it.vige.labs.gc.bean.result.Candidate[0])))
						.allMatch(f -> f.getId() == 171 || f.getId() == 172 || f.getId() == 173));
				assertTrue(e.getMapParties().values().stream()
						.flatMap(f -> stream(
								f.getMapCandidates().values().toArray(new it.vige.labs.gc.bean.result.Candidate[0])))
						.allMatch(f -> f.getElectors() == 1));
				assertEquals(0, e.getBlankPapers(), "no blank papers");
			}
		});
		setState(PREPARE);
	}

	@Test
	public void onlySelection() throws Exception {

		setState(VOTE);
		Group matteoSalvini = new Group(95);
		VotingPaper nazionali = new VotingPaper(86, null, matteoSalvini);
		VotingPaper comunali = new VotingPaper(0);
		VotingPaper regionali = new VotingPaper(11);
		VotingPaper europee = new VotingPaper(121);

		Vote vote = new Vote(new ArrayList<VotingPaper>(asList(new VotingPaper[] { nazionali })));
		Messages messages = voteController.vote(vote);
		logger.info(messages + "");
		assertFalse(messages.isOk(), "we need to send all the voting papers");

		vote.getVotingPapers().add(comunali);
		vote.getVotingPapers().add(regionali);
		vote.getVotingPapers().add(europee);
		messages = voteController.vote(vote);
		logger.info(messages + "");
		assertArrayEquals(defaultMessage.getMessages().toArray(), messages.getMessages().toArray(),
				"only a group without party is ok");
		assertTrue(messages.isOk());

		VotingPapers votingPapers = voteController.getResult(new VoteRequest(vote, new VotingPapers())).getVotings()
				.get(0);
		assertTrue(votingPapers.getElectors() == 1);
		assertTrue(votingPapers.getMapVotingPapers().size() == 4);
		assertTrue(votingPapers.getMapVotingPapers().values().stream().allMatch(e -> e.getElectors() == 1));
		assertTrue(votingPapers.getMapVotingPapers().values().stream()
				.allMatch(e -> e.getId() == 0 || e.getId() == 86 || e.getId() == 11 || e.getId() == 121));
		votingPapers.getMapVotingPapers().values().stream().forEach(e -> {
			if (e.getId() == 86) {
				assertTrue(e.getMapGroups().values().stream().filter(f -> f.getId() == 95).findFirst().get()
						.getElectors() == 1);
				assertTrue(e.getMapGroups().values().stream().flatMap(
						f -> stream(f.getMapParties().values().toArray(new it.vige.labs.gc.bean.result.Party[0])))
						.count() == 0);
				assertEquals(0, e.getBlankPapers(), "no blank papers");
			}
		});

		nazionali.setGroup(null);
		messages = voteController.vote(vote);
		logger.info(messages + "");
		assertArrayEquals(defaultMessage.getMessages().toArray(), messages.getMessages().toArray(),
				"no group and no party is a blank paper");
		assertTrue(messages.isOk());

		Party fratelliDItalia = new Party(96);
		nazionali.setParty(fratelliDItalia);
		messages = voteController.vote(vote);
		logger.info(messages + "");
		assertArrayEquals(errorMessage.getMessages().toArray(), messages.getMessages().toArray(),
				"a party without group is not ok if not disjointed");
		assertFalse(messages.isOk());

		Group michelBarbet = new Group(5);
		comunali.setGroup(michelBarbet);
		vote = new Vote(new ArrayList<VotingPaper>(asList(new VotingPaper[] { comunali })));
		vote.getVotingPapers().add(new VotingPaper(86));
		vote.getVotingPapers().add(regionali);
		vote.getVotingPapers().add(europee);
		messages = voteController.vote(vote);
		logger.info(messages + "");
		assertArrayEquals(defaultMessage.getMessages().toArray(), messages.getMessages().toArray(),
				"only a group without party is ok");
		assertTrue(messages.isOk());

		votingPapers = voteController.getResult(new VoteRequest(vote, new VotingPapers())).getVotings().get(0);
		assertTrue(votingPapers.getElectors() == 1);
		assertTrue(votingPapers.getMapVotingPapers().size() == 4);
		assertTrue(votingPapers.getMapVotingPapers().values().stream().allMatch(e -> e.getElectors() == 1));
		assertTrue(votingPapers.getMapVotingPapers().values().stream()
				.allMatch(e -> e.getId() == 0 || e.getId() == 86 || e.getId() == 11 || e.getId() == 121));
		votingPapers.getMapVotingPapers().values().stream().forEach(e -> {
			if (e.getId() == 0) {
				assertTrue(e.getMapGroups().values().stream().filter(f -> f.getId() == 5).findFirst().get()
						.getElectors() == 1);
				assertTrue(e.getMapGroups().values().stream().flatMap(
						f -> stream(f.getMapParties().values().toArray(new it.vige.labs.gc.bean.result.Party[0])))
						.count() == 0);
				assertEquals(0, e.getBlankPapers(), "no blank papers");
			}
		});

		comunali.setGroup(null);
		messages = voteController.vote(vote);
		logger.info(messages + "");
		assertArrayEquals(defaultMessage.getMessages().toArray(), messages.getMessages().toArray(),
				"no group and no party is a blank paper");
		assertTrue(messages.isOk());

		Party pd = new Party(3);
		comunali.setParty(pd);
		messages = voteController.vote(vote);
		logger.info(messages + "");
		assertArrayEquals(defaultMessage.getMessages().toArray(), messages.getMessages().toArray(),
				"a party without group is ok if disjointed");
		assertTrue(messages.isOk());

		votingPapers = voteController.getResult(new VoteRequest(vote, new VotingPapers())).getVotings().get(0);
		assertTrue(votingPapers.getElectors() == 1);
		assertTrue(votingPapers.getMapVotingPapers().size() == 4);
		assertTrue(votingPapers.getMapVotingPapers().values().stream().allMatch(e -> e.getElectors() == 1));
		assertTrue(votingPapers.getMapVotingPapers().values().stream()
				.allMatch(e -> e.getId() == 0 || e.getId() == 86 || e.getId() == 11 || e.getId() == 121));
		votingPapers.getMapVotingPapers().values().stream().forEach(e -> {
			if (e.getId() == 0) {
				assertTrue(e.getMapGroups().isEmpty());
				assertTrue(e.getMapParties().values().stream().filter(f -> f.getId() == 3).findFirst().get()
						.getElectors() == 1);
				assertFalse(e.getMapParties().values().stream()
						.flatMap(f -> stream(
								f.getMapCandidates().values().toArray(new it.vige.labs.gc.bean.result.Candidate[0])))
						.count() == 1);
				assertTrue(e.getMapParties().values().stream()
						.flatMap(f -> stream(
								f.getMapCandidates().values().toArray(new it.vige.labs.gc.bean.result.Candidate[0])))
						.allMatch(f -> f.getId() == 171 || f.getId() == 172 || f.getId() == 173));
				assertTrue(e.getMapParties().values().stream()
						.flatMap(f -> stream(
								f.getMapCandidates().values().toArray(new it.vige.labs.gc.bean.result.Candidate[0])))
						.allMatch(f -> f.getElectors() == 1));
				assertEquals(0, e.getBlankPapers(), "no blank papers");
			}
		});
		setState(PREPARE);
	}

	@Test
	public void ids() throws Exception {

		setState(VOTE);
		Party giorgiaMeloni = new Party(93);
		Group matteoSalvini = new Group(95);
		VotingPaper nazionali = new VotingPaper(86, giorgiaMeloni, matteoSalvini);

		Vote vote = new Vote(asList(new VotingPaper[] { nazionali }));
		Messages messages = voteController.vote(vote);
		logger.info(messages + "");
		assertArrayEquals(errorMessage.getMessages().toArray(), messages.getMessages().toArray(),
				"the ids are inverted");
		assertFalse(messages.isOk());

		Party noiConSalvini = new Party(94);
		matteoSalvini.setId(1122);
		nazionali.setParty(noiConSalvini);
		messages = voteController.vote(vote);
		logger.info(messages + "");
		assertArrayEquals(errorMessage.getMessages().toArray(), messages.getMessages().toArray(),
				"the id doesn't exist");
		assertFalse(messages.isOk());
		setState(PREPARE);
	}

	@Test
	public void disjointed() throws Exception {

		setState(VOTE);
		Party giorgiaMeloni = new Party(95);
		Group matteoSalvini = new Group(93);
		VotingPaper nazionali = new VotingPaper(86, giorgiaMeloni, matteoSalvini);

		Vote vote = new Vote(asList(new VotingPaper[] { nazionali }));
		Messages messages = voteController.vote(vote);
		logger.info(messages + "");
		assertArrayEquals(errorMessage.getMessages().toArray(), messages.getMessages().toArray(),
				"the vote is not disjointed, you can select only the group of the party");
		assertFalse(messages.isOk());
		setState(PREPARE);
	}

	@Test
	public void candidates() throws Exception {

		setState(VOTE);
		VotingPaper comunali = new VotingPaper(0);
		VotingPaper regionali = new VotingPaper(11);
		VotingPaper nazionali = new VotingPaper(86);
		VotingPaper europee = new VotingPaper(121);

		Candidate giulianoSantoboni = new Candidate(30);
		Candidate matteoCastorino = new Candidate(32);
		Party movimento5Stelle = new Party(28,
				new ArrayList<Candidate>(asList(new Candidate[] { giulianoSantoboni, matteoCastorino })));
		Group forzaItalia = new Group(12);
		regionali.setGroup(forzaItalia);
		regionali.setParty(movimento5Stelle);

		Vote vote = new Vote(new ArrayList<VotingPaper>(asList(new VotingPaper[] { regionali })));
		Messages messages = voteController.vote(vote);
		logger.info(messages + "");
		assertArrayEquals(errorMessage.getMessages().toArray(), messages.getMessages().toArray(),
				"we need to send all the voting papers");
		assertFalse(messages.isOk());

		vote.getVotingPapers().add(comunali);
		vote.getVotingPapers().add(nazionali);
		vote.getVotingPapers().add(europee);
		messages = voteController.vote(vote);
		logger.info(messages + "");
		assertArrayEquals(errorMessage.getMessages().toArray(), messages.getMessages().toArray(),
				"there are two M and no F");
		assertFalse(messages.isOk());

		movimento5Stelle.getCandidates().remove(0);
		messages = voteController.vote(vote);
		logger.info(messages + "");
		assertArrayEquals(defaultMessage.getMessages().toArray(), messages.getMessages().toArray(),
				"there is only a candidate, it's ok");
		assertTrue(messages.isOk());

		VotingPapers votingPapers = voteController.getResult(new VoteRequest(vote, new VotingPapers())).getVotings()
				.get(0);
		assertTrue(votingPapers.getElectors() == 1);
		assertTrue(votingPapers.getMapVotingPapers().size() == 4);
		assertTrue(votingPapers.getMapVotingPapers().values().stream().allMatch(e -> e.getElectors() == 1));
		assertTrue(votingPapers.getMapVotingPapers().values().stream()
				.allMatch(e -> e.getId() == 0 || e.getId() == 86 || e.getId() == 11 || e.getId() == 121));
		votingPapers.getMapVotingPapers().values().stream().forEach(e -> {
			if (e.getId() == 11) {
				assertTrue(e.getMapGroups().values().stream().filter(f -> f.getId() == 12).findFirst().get()
						.getElectors() == 1);
				assertTrue(e.getMapGroups().values().stream().flatMap(
						f -> stream(f.getMapParties().values().toArray(new it.vige.labs.gc.bean.result.Party[0])))
						.allMatch(g -> g.getId() == 28 && g.getElectors() == 1));
				assertFalse(e.getMapGroups().values().stream().flatMap(
						f -> stream(f.getMapParties().values().toArray(new it.vige.labs.gc.bean.result.Party[0])))
						.flatMap(f -> stream(
								f.getMapCandidates().values().toArray(new it.vige.labs.gc.bean.result.Candidate[0])))
						.count() == 0);
				assertTrue(e.getMapGroups().values().stream().flatMap(
						f -> stream(f.getMapParties().values().toArray(new it.vige.labs.gc.bean.result.Party[0])))
						.flatMap(f -> stream(
								f.getMapCandidates().values().toArray(new it.vige.labs.gc.bean.result.Candidate[0])))
						.allMatch(f -> f.getId() == 32));
				assertTrue(e.getMapGroups().values().stream().flatMap(
						f -> stream(f.getMapParties().values().toArray(new it.vige.labs.gc.bean.result.Party[0])))
						.flatMap(f -> stream(
								f.getMapCandidates().values().toArray(new it.vige.labs.gc.bean.result.Candidate[0])))
						.allMatch(f -> f.getElectors() == 1));
				assertEquals(0, e.getBlankPapers(), "no blank papers");
			}
		});

		Candidate paolaTaverna = new Candidate(31);
		movimento5Stelle.getCandidates().add(giulianoSantoboni);
		movimento5Stelle.getCandidates().add(paolaTaverna);
		messages = voteController.vote(vote);
		logger.info(messages + "");
		assertArrayEquals(errorMessage.getMessages().toArray(), messages.getMessages().toArray(),
				"candidates number exceed the max candidates value");
		assertFalse(messages.isOk());

		movimento5Stelle.getCandidates().clear();
		messages = voteController.vote(vote);
		logger.info(messages + "");
		assertArrayEquals(defaultMessage.getMessages().toArray(), messages.getMessages().toArray(),
				"no candidates, the result is ok");
		assertTrue(messages.isOk());
		setState(PREPARE);
	}

}
