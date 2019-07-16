package it.vige.labs.gc;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import it.vige.labs.gc.domain.Candidate;
import it.vige.labs.gc.domain.Group;
import it.vige.labs.gc.domain.Party;
import it.vige.labs.gc.domain.Vote;
import it.vige.labs.gc.domain.VotingPaper;
import it.vige.labs.gc.rest.VoteController;
import it.vige.labs.gc.result.Messages;

@RunWith(SpringRunner.class)
@SpringBootTest
public class VoteTest {

	Logger logger = LoggerFactory.getLogger(VoteTest.class);

	@Autowired
	private VoteController voteController;

	@Test
	public void vote() throws IOException {

		Party pd = new Party(3);
		Group michelBarbet = new Group(6);
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
		Assert.assertArrayEquals("the result is ok", VoteController.defaultMessage.getMessages().toArray(),
				messages.getMessages().toArray());
	}

}
