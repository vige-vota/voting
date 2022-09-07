package it.vige.labs.gc;

import static it.vige.labs.gc.rest.Validator.defaultMessage;
import static it.vige.labs.gc.rest.Validator.errorMessage;
import static it.vige.labs.gc.users.Authorities.ADMIN_ROLE;
import static it.vige.labs.gc.users.Authorities.CITIZEN_ROLE;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Calendar.DATE;
import static java.util.Calendar.getInstance;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.util.UriComponentsBuilder.newInstance;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import com.c4_soft.springaddons.security.oauth2.test.annotations.OpenIdClaims;
import com.c4_soft.springaddons.security.oauth2.test.annotations.keycloak.WithMockKeycloakAuth;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.vige.labs.gc.bean.VoteRequest;
import it.vige.labs.gc.bean.result.VotingPapers;
import it.vige.labs.gc.bean.vote.Candidate;
import it.vige.labs.gc.bean.vote.Group;
import it.vige.labs.gc.bean.vote.Party;
import it.vige.labs.gc.bean.vote.Vote;
import it.vige.labs.gc.bean.vote.VotingPaper;
import it.vige.labs.gc.bean.votingpapers.VotingDate;
import it.vige.labs.gc.messages.Messages;
import it.vige.labs.gc.rest.Validator;
import it.vige.labs.gc.rest.VoteController;
import it.vige.labs.gc.users.Authorities;

@SpringBootTest(webEnvironment = DEFINED_PORT)
@ActiveProfiles("dev")
public class VoteTest {

	private Logger logger = getLogger(VoteTest.class);

	private final static String DEFAULT_USER = "669d3be4-4a67-41f5-a49d-5fe5157b6dd5";

	private it.vige.labs.gc.bean.votingpapers.VotingPapers votingPapers;

	private UserRepresentation user = new UserRepresentation();

	@Autowired
	private VoteController voteController;

	@Autowired
	private Validator validator;

	@Mock
	private RestTemplate restTemplate;

	@Autowired
	private Authorities authorities;

	@Value("${votingpapers.scheme}")
	private String votingpapersScheme;

	@Value("${votingpapers.host}")
	private String votingpapersHost;

	@Value("${votingpapers.port}")
	private int votingpapersPort;

	@BeforeEach
	public void init() throws Exception {
		mockVotingPapers();
		mockUsers();
	}

	@Test
	@WithMockKeycloakAuth(authorities = { ADMIN_ROLE }, claims = @OpenIdClaims(preferredUsername = DEFAULT_USER))
	public void voteOk() throws Exception {

		changeZone("4-2523228-2523962-6542276");
		Party pd = new Party(3);
		Group michelBarbet = new Group(5);
		VotingPaper comunali = new VotingPaper(0, asList(new Party[] { pd }), michelBarbet);

		Candidate paolaTaverna = new Candidate(31);
		Candidate matteoCastorino = new Candidate(32);
		Party movimento5Stelle = new Party(28, asList(new Candidate[] { paolaTaverna, matteoCastorino }));
		Group forzaItalia = new Group(12);
		VotingPaper regionali = new VotingPaper(11, asList(new Party[] { movimento5Stelle }), forzaItalia);

		Party noiConSalvini = new Party(94);
		Group matteoSalvini = new Group(93);
		VotingPaper nazionali = new VotingPaper(86, asList(new Party[] { noiConSalvini }), matteoSalvini);

		Candidate giorgiaMeloni = new Candidate(171);
		Candidate francescoAcquaroli = new Candidate(172);
		Candidate ariannaAlessandrini = new Candidate(173);
		Party fratelliDitalia = new Party(127,
				asList(new Candidate[] { giorgiaMeloni, francescoAcquaroli, ariannaAlessandrini }));
		VotingPaper europee = new VotingPaper(121, asList(new Party[] { fratelliDitalia }));

		Party si = new Party(364);
		VotingPaper popolari = new VotingPaper(260, new ArrayList<Party>(asList(new Party[] { si })));

		Vote vote = new Vote(asList(new VotingPaper[] { comunali, regionali, nazionali, europee, popolari }));
		Messages messages = voteController.vote(vote);
		logger.info(messages + "");
		assertFalse(messages.isOk(), "the voting paper is expired");
		votingPapers.getVotingPapers().forEach(e -> {
			if (e.getId() == 121)
				addDates(e, -1, 3);
		});
		voteController.resetVotingPapers();

		Group antiCorruzione = new Group(363);
		popolari.setGroup(antiCorruzione);
		messages = voteController.vote(vote);
		assertFalse(messages.isOk(), "referendum cannot have a group to vote");

		popolari.setGroup(null);
		popolari.getParties().add(new Party(366));
		popolari.getParties().add(new Party(369));
		Party wrongParty = new Party(243);
		popolari.getParties().add(wrongParty);
		messages = voteController.vote(vote);
		assertFalse(messages.isOk(), "we cannot add a party from a different group");
		
		popolari.getParties().remove(wrongParty);
		messages = voteController.vote(vote);
		assertArrayEquals(defaultMessage.getMessages().toArray(), messages.getMessages().toArray(), "the result is ok");
		assertTrue(messages.isOk());

		VotingPapers votingPapers = voteController.getResult(new VoteRequest(vote, new VotingPapers())).getVotings()
				.get(0);
		assertTrue(votingPapers.getElectors() == 1);
		assertTrue(votingPapers.getMapVotingPapers().size() == 5);
		assertTrue(votingPapers.getMapVotingPapers().values().stream().allMatch(e -> e.getElectors() == 1));
		votingPapers.getMapVotingPapers().values().stream().forEach(e -> {
			assertTrue(e.getId() == 0 || e.getId() == 11 || e.getId() == 86 || e.getId() == 121 || e.getId() == 260);
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
			if (e.getId() == 260) {
				assertTrue(e.getMapGroups().isEmpty());
				assertTrue(e.getMapParties().values().stream().allMatch(g -> g.getElectors() == 1));
				assertTrue(e.getMapParties().values().stream().anyMatch(g -> g.getId() == 364));
				assertTrue(e.getMapParties().values().stream().anyMatch(g -> g.getId() == 366));
				assertTrue(e.getMapParties().values().stream().anyMatch(g -> g.getId() == 369));
				assertEquals(0, e.getBlankPapers(), "no blank papers");
			}
		});
	}

	@Test
	@WithMockKeycloakAuth(authorities = { CITIZEN_ROLE }, claims = @OpenIdClaims(preferredUsername = DEFAULT_USER))
	public void onlySelection() throws Exception {

		changeZone("4-2523228-2523962-6542276");
		Group matteoSalvini = new Group(95);
		VotingPaper nazionali = new VotingPaper(86, null, matteoSalvini);
		VotingPaper comunali = new VotingPaper(0);
		VotingPaper regionali = new VotingPaper(11);
		VotingPaper europee = new VotingPaper(121);
		VotingPaper popolare = new VotingPaper(260);
		VotingPaper wrong = new VotingPaper(121000);

		Vote vote = new Vote(new ArrayList<VotingPaper>(
				asList(new VotingPaper[] { nazionali, comunali, regionali, europee, popolare, wrong })));
		Messages messages = voteController.vote(vote);
		logger.info(messages + "");
		assertFalse(messages.isOk(), "we cannot send more voting papers then the exposed");

		vote.getVotingPapers().remove(5);
		messages = voteController.vote(vote);
		logger.info(messages + "");
		assertArrayEquals(defaultMessage.getMessages().toArray(), messages.getMessages().toArray(),
				"only a group without party is ok");
		assertTrue(messages.isOk());

		VotingPapers votingPapers = voteController.getResult(new VoteRequest(vote, new VotingPapers())).getVotings()
				.get(0);
		assertTrue(votingPapers.getElectors() == 1);
		assertTrue(votingPapers.getMapVotingPapers().size() == 5);
		assertTrue(votingPapers.getMapVotingPapers().values().stream().allMatch(e -> e.getElectors() == 1));
		assertTrue(votingPapers.getMapVotingPapers().values().stream().allMatch(
				e -> e.getId() == 0 || e.getId() == 86 || e.getId() == 11 || e.getId() == 121 || e.getId() == 260));
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
		nazionali.setParties(asList(new Party[] { fratelliDItalia }));
		messages = voteController.vote(vote);
		logger.info(messages + "");
		assertArrayEquals(errorMessage.getMessages().toArray(), messages.getMessages().toArray(), "the user has voted");
		changeZone("4-2523228-2523962-6542276");

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
		comunali.setParties(asList(new Party[] { pd }));
		messages = voteController.vote(vote);
		logger.info(messages + "");
		assertArrayEquals(errorMessage.getMessages().toArray(), messages.getMessages().toArray(), "the user has voted");
		changeZone("4-2523228-2523962-6542276");

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
	}

	@Test
	@WithMockKeycloakAuth(authorities = { CITIZEN_ROLE }, claims = @OpenIdClaims(preferredUsername = DEFAULT_USER))
	public void authorized() throws Exception {

		changeZone("4-2523228-2523962-6542277");
		VotingPaper comunali = new VotingPaper(0);
		Group michelBarbet = new Group(5);
		comunali.setGroup(michelBarbet);
		Vote vote = new Vote(new ArrayList<VotingPaper>(asList(new VotingPaper[] { comunali })));
		Messages messages = voteController.vote(vote);
		logger.info(messages + "");
		assertArrayEquals(errorMessage.getMessages().toArray(), messages.getMessages().toArray(),
				"user is not authorized to vote the voting paper");
		assertFalse(messages.isOk());

		changeZone("4-2523228-2523962-6542276");
		messages = voteController.vote(vote);
		logger.info(messages + "");
		assertArrayEquals(defaultMessage.getMessages().toArray(), messages.getMessages().toArray(),
				"only a group without party is ok");
		assertTrue(messages.isOk());
	}

	@Test
	@WithMockKeycloakAuth(authorities = { CITIZEN_ROLE }, claims = @OpenIdClaims(preferredUsername = DEFAULT_USER))
	public void ids() throws Exception {

		changeZone("-1");
		Party giorgiaMeloni = new Party(93);
		Group matteoSalvini = new Group(95);
		VotingPaper nazionali = new VotingPaper(86, asList(new Party[] { giorgiaMeloni }), matteoSalvini);

		Vote vote = new Vote(asList(new VotingPaper[] { nazionali }));
		Messages messages = voteController.vote(vote);
		logger.info(messages + "");
		assertArrayEquals(errorMessage.getMessages().toArray(), messages.getMessages().toArray(),
				"the ids are inverted");
		assertFalse(messages.isOk());

		Party noiConSalvini = new Party(94);
		matteoSalvini.setId(1122);
		nazionali.setParties(asList(new Party[] { noiConSalvini }));
		messages = voteController.vote(vote);
		logger.info(messages + "");
		assertArrayEquals(errorMessage.getMessages().toArray(), messages.getMessages().toArray(),
				"the id doesn't exist");
		assertFalse(messages.isOk());
	}

	@Test
	@WithMockKeycloakAuth(authorities = { CITIZEN_ROLE }, claims = @OpenIdClaims(preferredUsername = DEFAULT_USER))
	public void disjointed() throws Exception {

		changeZone("-1");
		Party giorgiaMeloni = new Party(95);
		Group matteoSalvini = new Group(93);
		VotingPaper nazionali = new VotingPaper(86, asList(new Party[] { giorgiaMeloni }), matteoSalvini);

		Vote vote = new Vote(asList(new VotingPaper[] { nazionali }));
		Messages messages = voteController.vote(vote);
		logger.info(messages + "");
		assertArrayEquals(errorMessage.getMessages().toArray(), messages.getMessages().toArray(),
				"the vote is not disjointed, you can select only the group of the party");
		assertFalse(messages.isOk());
	}

	@Test
	@WithMockKeycloakAuth(authorities = { CITIZEN_ROLE }, claims = @OpenIdClaims(preferredUsername = DEFAULT_USER))
	public void candidates() throws Exception {

		changeZone("4-2523228-2523962-6542276");
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
		regionali.setParties(asList(new Party[] { movimento5Stelle }));

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
		assertArrayEquals(errorMessage.getMessages().toArray(), messages.getMessages().toArray(), "the user has voted");
		changeZone("4-2523228-2523962-6542276");

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
	}

	private void addDates(it.vige.labs.gc.bean.votingpapers.VotingPaper votingPaper, int startingDays, int endingDays) {
		List<VotingDate> dates = votingPaper.getDates();
		if (dates == null)
			votingPaper.setDates(new ArrayList<VotingDate>());
		Date startingDate = addDays(new Date(), startingDays);
		VotingDate votingDate = new VotingDate();
		votingDate.setStartingDate(startingDate);
		votingDate.setEndingDate(addDays(startingDate, endingDays));
		votingPaper.getDates().add(votingDate);
	}

	private Date addDays(Date date, int days) {
		Calendar cal = getInstance();
		cal.setTime(date);
		cal.add(DATE, days); // minus number would decrement the days
		return cal.getTime();
	}

	private void mockVotingPapers() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		InputStream jsonStream = new FileInputStream("src/test/resources/mock/config-app.json");
		votingPapers = objectMapper.readValue(jsonStream, it.vige.labs.gc.bean.votingpapers.VotingPapers.class);
		votingPapers.getVotingPapers().forEach(e -> {
			if (e.getId() != 121)
				addDates(e, -1, 3);
			else
				addDates(e, -3, -2);
		});
		String url = newInstance().scheme(votingpapersScheme).host(votingpapersHost).port(votingpapersPort)
				.path("/votingPapers?info&all").buildAndExpand().toString();
		when(restTemplate.exchange(url, GET, null, it.vige.labs.gc.bean.votingpapers.VotingPapers.class))
				.thenReturn(new ResponseEntity<it.vige.labs.gc.bean.votingpapers.VotingPapers>(votingPapers, OK));
		validator.setRestTemplate(restTemplate);
		voteController.setValidator(validator);
	}

	private void mockUsers() {
		user.setUsername(DEFAULT_USER);
		when(restTemplate.exchange(authorities.getFindUserURI().toString(), GET, null, UserRepresentation.class))
				.thenReturn(new ResponseEntity<UserRepresentation>(user, OK));
		authorities.setRestTemplate(restTemplate);
		voteController.setAuthorities(authorities);
	}

	private void changeZone(String zones) {
		Map<String, List<String>> attributes = new HashMap<String, List<String>>();
		attributes.put("zones", asList(zones));
		user.setAttributes(attributes);
	}

}
