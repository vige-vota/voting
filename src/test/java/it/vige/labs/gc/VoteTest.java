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

import it.vige.labs.gc.rest.Validator;
import it.vige.labs.gc.rest.VoteController;
import it.vige.labs.gc.result.Messages;
import it.vige.labs.gc.vote.Candidate;
import it.vige.labs.gc.vote.Group;
import it.vige.labs.gc.vote.Party;
import it.vige.labs.gc.vote.Vote;
import it.vige.labs.gc.vote.VotingPaper;

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
	}

	@Test
	public void ids() {

		Party giorgiaMeloni = new Party(93);
		Group matteoSalvini = new Group(95);
		VotingPaper nazionali = new VotingPaper(86, giorgiaMeloni, matteoSalvini);

		Vote vote = new Vote(Arrays.asList(new VotingPaper[] { nazionali }));
		Messages messages = voteController.vote(vote);
		logger.info(messages + "");
		Assert.assertArrayEquals("the ids are inverted",
				Validator.errorMessage.getMessages().toArray(), messages.getMessages().toArray());
		Assert.assertFalse(messages.isOk());
		
		Party noiConSalvini = new Party(94);
		matteoSalvini.setId(1122);
		nazionali.setParty(noiConSalvini);
		messages = voteController.vote(vote);
		logger.info(messages + "");
		Assert.assertArrayEquals("the id doesn't exist",
				Validator.errorMessage.getMessages().toArray(), messages.getMessages().toArray());
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
