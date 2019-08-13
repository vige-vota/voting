package it.vige.labs.gc;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import it.vige.labs.gc.bean.VoteRequest;
import it.vige.labs.gc.bean.result.VotingPapers;
import it.vige.labs.gc.bean.vote.Candidate;
import it.vige.labs.gc.bean.vote.Group;
import it.vige.labs.gc.bean.vote.Party;
import it.vige.labs.gc.bean.vote.Vote;
import it.vige.labs.gc.bean.vote.VotingPaper;
import it.vige.labs.gc.messages.Messages;
import it.vige.labs.gc.rest.Validator;
import it.vige.labs.gc.rest.VoteController;

@RunWith(SpringRunner.class)
@SpringBootTest
public class VoteTest {

	private Logger logger = LoggerFactory.getLogger(VoteTest.class);

	@Autowired
	private VoteController voteController;

	@Test
	public void voteOk() {

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

		VotingPapers votingPapers = voteController.getResult(new VoteRequest(vote, new VotingPapers()));
		Assert.assertTrue(votingPapers.getElectors() == 1);
		Assert.assertTrue(votingPapers.getMapVotingPapers().size() == 4);
		Assert.assertTrue(votingPapers.getMapVotingPapers().values().stream().allMatch(e -> e.getElectors() == 1));
		votingPapers.getMapVotingPapers().values().stream().forEach(e -> {
			Assert.assertTrue(e.getId() == 0 || e.getId() == 11 || e.getId() == 86 || e.getId() == 121);
			if (e.getId() == 0) {
				Assert.assertTrue(e.getMapGroups().values().stream().filter(f -> f.getId() == 5).findFirst().get()
						.getVotes() == 1);
				Assert.assertTrue(e.getMapGroups().values().stream()
						.flatMap(f -> Arrays
								.stream(f.getMapParties().values().toArray(new it.vige.labs.gc.bean.result.Party[0])))
						.allMatch(g -> g.getId() == 3 && g.getVotes() == 1));
				Assert.assertTrue(e.getMapGroups().values().stream()
						.flatMap(f -> Arrays
								.stream(f.getMapParties().values().toArray(new it.vige.labs.gc.bean.result.Party[0])))
						.flatMap(f -> Arrays.stream(
								f.getMapCandidates().values().toArray(new it.vige.labs.gc.bean.result.Candidate[0])))
						.count() == 0);
			}
			if (e.getId() == 11) {
				Assert.assertTrue(e.getMapGroups().values().stream().filter(f -> f.getId() == 12).findFirst().get()
						.getVotes() == 1);
				Assert.assertTrue(e.getMapGroups().values().stream()
						.flatMap(f -> Arrays
								.stream(f.getMapParties().values().toArray(new it.vige.labs.gc.bean.result.Party[0])))
						.allMatch(g -> g.getId() == 28 && g.getVotes() == 1));
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
						.allMatch(f -> f.getVotes() == 1));
			}
			if (e.getId() == 86) {
				Assert.assertTrue(e.getMapGroups().values().stream().filter(f -> f.getId() == 93).findFirst().get()
						.getVotes() == 1);
				Assert.assertTrue(e.getMapGroups().values().stream()
						.flatMap(f -> Arrays
								.stream(f.getMapParties().values().toArray(new it.vige.labs.gc.bean.result.Party[0])))
						.anyMatch(g -> g.getId() == 94 && g.getVotes() == 1));
				Assert.assertTrue(e.getMapGroups().values().stream()
						.flatMap(f -> Arrays
								.stream(f.getMapParties().values().toArray(new it.vige.labs.gc.bean.result.Party[0])))
						.flatMap(f -> Arrays.stream(
								f.getMapCandidates().values().toArray(new it.vige.labs.gc.bean.result.Candidate[0])))
						.count() == 0);
			}
			if (e.getId() == 121) {
				Assert.assertTrue(e.getMapGroups().values().stream().allMatch(f -> f.isEmpty() == true));
				Assert.assertTrue(e.getMapGroups().values().stream()
						.flatMap(f -> Arrays
								.stream(f.getMapParties().values().toArray(new it.vige.labs.gc.bean.result.Party[0])))
						.allMatch(g -> g.getId() == 127 && g.getVotes() == 1));
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
						.allMatch(f -> f.getId() == 171 || f.getId() == 172 || f.getId() == 173));
				Assert.assertTrue(e.getMapGroups().values().stream()
						.flatMap(f -> Arrays
								.stream(f.getMapParties().values().toArray(new it.vige.labs.gc.bean.result.Party[0])))
						.flatMap(f -> Arrays.stream(
								f.getMapCandidates().values().toArray(new it.vige.labs.gc.bean.result.Candidate[0])))
						.allMatch(f -> f.getVotes() == 1));
			}
		});
	}

	@Test
	public void onlySelection() {

		Group matteoSalvini = new Group(95);
		VotingPaper nazionali = new VotingPaper(86, null, matteoSalvini);

		Vote vote = new Vote(Arrays.asList(new VotingPaper[] { nazionali }));
		Messages messages = voteController.vote(vote);
		logger.info(messages + "");
		Assert.assertArrayEquals("only a group without party is ok", Validator.defaultMessage.getMessages().toArray(),
				messages.getMessages().toArray());
		Assert.assertTrue(messages.isOk());

		VotingPapers votingPapers = voteController.getResult(new VoteRequest(vote, new VotingPapers()));
		Assert.assertTrue(votingPapers.getElectors() == 1);
		Assert.assertTrue(votingPapers.getMapVotingPapers().size() == 1);
		Assert.assertTrue(votingPapers.getMapVotingPapers().values().stream().allMatch(e -> e.getElectors() == 1));
		votingPapers.getMapVotingPapers().values().stream().forEach(e -> {
			Assert.assertTrue(e.getId() == 86);
			Assert.assertTrue(e.getMapGroups().values().stream().filter(f -> f.getId() == 95).findFirst().get()
					.getVotes() == 1);
			Assert.assertTrue(e.getMapGroups().values().stream().flatMap(
					f -> Arrays.stream(f.getMapParties().values().toArray(new it.vige.labs.gc.bean.result.Party[0])))
					.count() == 0);
		});

		nazionali.setGroup(null);
		messages = voteController.vote(vote);
		logger.info(messages + "");
		Assert.assertArrayEquals("no group and no party is not ok", Validator.errorMessage.getMessages().toArray(),
				messages.getMessages().toArray());
		Assert.assertFalse(messages.isOk());

		Party fratelliDItalia = new Party(96);
		nazionali.setParty(fratelliDItalia);
		messages = voteController.vote(vote);
		logger.info(messages + "");
		Assert.assertArrayEquals("a party without group is not ok if not disjointed",
				Validator.errorMessage.getMessages().toArray(), messages.getMessages().toArray());
		Assert.assertFalse(messages.isOk());

		Group michelBarbet = new Group(5);
		VotingPaper comunali = new VotingPaper(0, null, michelBarbet);
		vote = new Vote(Arrays.asList(new VotingPaper[] { comunali }));
		messages = voteController.vote(vote);
		logger.info(messages + "");
		Assert.assertArrayEquals("only a group without party is ok", Validator.defaultMessage.getMessages().toArray(),
				messages.getMessages().toArray());
		Assert.assertTrue(messages.isOk());

		votingPapers = voteController.getResult(new VoteRequest(vote, new VotingPapers()));
		Assert.assertTrue(votingPapers.getElectors() == 1);
		Assert.assertTrue(votingPapers.getMapVotingPapers().size() == 1);
		Assert.assertTrue(votingPapers.getMapVotingPapers().values().stream().allMatch(e -> e.getElectors() == 1));
		votingPapers.getMapVotingPapers().values().stream().forEach(e -> {
			Assert.assertTrue(e.getId() == 0);
			Assert.assertTrue(e.getMapGroups().values().stream().filter(f -> f.getId() == 5).findFirst().get()
					.getVotes() == 1);
			Assert.assertTrue(e.getMapGroups().values().stream().flatMap(
					f -> Arrays.stream(f.getMapParties().values().toArray(new it.vige.labs.gc.bean.result.Party[0])))
					.count() == 0);
		});

		comunali.setGroup(null);
		messages = voteController.vote(vote);
		logger.info(messages + "");
		Assert.assertArrayEquals("no group and no party is not ok", Validator.errorMessage.getMessages().toArray(),
				messages.getMessages().toArray());
		Assert.assertFalse(messages.isOk());

		Party pd = new Party(3);
		comunali.setParty(pd);
		messages = voteController.vote(vote);
		logger.info(messages + "");
		Assert.assertArrayEquals("a party without group is ok if disjointed",
				Validator.defaultMessage.getMessages().toArray(), messages.getMessages().toArray());
		Assert.assertTrue(messages.isOk());

		votingPapers = voteController.getResult(new VoteRequest(vote, new VotingPapers()));
		Assert.assertTrue(votingPapers.getElectors() == 1);
		Assert.assertTrue(votingPapers.getMapVotingPapers().size() == 1);
		Assert.assertTrue(votingPapers.getMapVotingPapers().values().stream().allMatch(e -> e.getElectors() == 1));
		votingPapers.getMapVotingPapers().values().stream().forEach(e -> {
			Assert.assertTrue(e.getId() == 0);
			Assert.assertTrue(e.getMapGroups().values().stream().filter(f -> f.getId() == 0).findFirst().get()
					.getVotes() == 1);
			Assert.assertTrue(e.getMapGroups().values().stream().flatMap(
					f -> Arrays.stream(f.getMapParties().values().toArray(new it.vige.labs.gc.bean.result.Party[0])))
					.allMatch(g -> g.getId() == 3));
			Assert.assertFalse(e.getMapGroups().values().stream().flatMap(
					f -> Arrays.stream(f.getMapParties().values().toArray(new it.vige.labs.gc.bean.result.Party[0])))
					.flatMap(f -> Arrays.stream(
							f.getMapCandidates().values().toArray(new it.vige.labs.gc.bean.result.Candidate[0])))
					.count() == 1);
			Assert.assertTrue(e.getMapGroups().values().stream().flatMap(
					f -> Arrays.stream(f.getMapParties().values().toArray(new it.vige.labs.gc.bean.result.Party[0])))
					.flatMap(f -> Arrays.stream(
							f.getMapCandidates().values().toArray(new it.vige.labs.gc.bean.result.Candidate[0])))
					.allMatch(f -> f.getId() == 171 || f.getId() == 172 || f.getId() == 173));
			Assert.assertTrue(e.getMapGroups().values().stream().flatMap(
					f -> Arrays.stream(f.getMapParties().values().toArray(new it.vige.labs.gc.bean.result.Party[0])))
					.flatMap(f -> Arrays.stream(
							f.getMapCandidates().values().toArray(new it.vige.labs.gc.bean.result.Candidate[0])))
					.allMatch(f -> f.getVotes() == 1));
		});
	}

	@Test
	public void ids() {

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
	}

	@Test
	public void disjointed() {

		Party giorgiaMeloni = new Party(95);
		Group matteoSalvini = new Group(93);
		VotingPaper nazionali = new VotingPaper(86, giorgiaMeloni, matteoSalvini);

		Vote vote = new Vote(Arrays.asList(new VotingPaper[] { nazionali }));
		Messages messages = voteController.vote(vote);
		logger.info(messages + "");
		Assert.assertArrayEquals("the vote is not disjointed, you can select only the group of the party",
				Validator.errorMessage.getMessages().toArray(), messages.getMessages().toArray());
		Assert.assertFalse(messages.isOk());
	}

	@Test
	public void candidates() {

		Candidate giulianoSantoboni = new Candidate(30);
		Candidate matteoCastorino = new Candidate(32);
		Party movimento5Stelle = new Party(28,
				new ArrayList<Candidate>(Arrays.asList(new Candidate[] { giulianoSantoboni, matteoCastorino })));
		Group forzaItalia = new Group(12);
		VotingPaper regionali = new VotingPaper(11, movimento5Stelle, forzaItalia);

		Vote vote = new Vote(Arrays.asList(new VotingPaper[] { regionali }));
		Messages messages = voteController.vote(vote);
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

		VotingPapers votingPapers = voteController.getResult(new VoteRequest(vote, new VotingPapers()));
		Assert.assertTrue(votingPapers.getElectors() == 1);
		Assert.assertTrue(votingPapers.getMapVotingPapers().size() == 1);
		Assert.assertTrue(votingPapers.getMapVotingPapers().values().stream().allMatch(e -> e.getElectors() == 1));
		votingPapers.getMapVotingPapers().values().stream().forEach(e -> {
			Assert.assertTrue(e.getId() == 11);
			Assert.assertTrue(e.getMapGroups().values().stream().filter(f -> f.getId() == 12).findFirst().get()
					.getVotes() == 1);
			Assert.assertTrue(e.getMapGroups().values().stream().flatMap(
					f -> Arrays.stream(f.getMapParties().values().toArray(new it.vige.labs.gc.bean.result.Party[0])))
					.allMatch(g -> g.getId() == 28 && g.getVotes() == 1));
			Assert.assertFalse(e.getMapGroups().values().stream().flatMap(
					f -> Arrays.stream(f.getMapParties().values().toArray(new it.vige.labs.gc.bean.result.Party[0])))
					.flatMap(f -> Arrays.stream(
							f.getMapCandidates().values().toArray(new it.vige.labs.gc.bean.result.Candidate[0])))
					.count() == 0);
			Assert.assertTrue(e.getMapGroups().values().stream().flatMap(
					f -> Arrays.stream(f.getMapParties().values().toArray(new it.vige.labs.gc.bean.result.Party[0])))
					.flatMap(f -> Arrays.stream(
							f.getMapCandidates().values().toArray(new it.vige.labs.gc.bean.result.Candidate[0])))
					.allMatch(f -> f.getId() == 32));
			Assert.assertTrue(e.getMapGroups().values().stream().flatMap(
					f -> Arrays.stream(f.getMapParties().values().toArray(new it.vige.labs.gc.bean.result.Party[0])))
					.flatMap(f -> Arrays.stream(
							f.getMapCandidates().values().toArray(new it.vige.labs.gc.bean.result.Candidate[0])))
					.allMatch(f -> f.getVotes() == 1));
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
	}

}
