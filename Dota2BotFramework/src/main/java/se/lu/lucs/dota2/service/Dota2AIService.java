package se.lu.lucs.dota2.service;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.iki.elonen.NanoHTTPD;
import se.lu.lucs.dota2.framework.bot.Bot;
import se.lu.lucs.dota2.framework.bot.Bot.Command;
import se.lu.lucs.dota2.framework.bot.BotCommands.LevelUp;
import se.lu.lucs.dota2.framework.bot.BotCommands.Select;
import se.lu.lucs.dota2.framework.game.ChatEvent;
import se.lu.lucs.dota2.framework.game.World;

/**
 * This class uses NanoHTTD to provide a very simple webservice The webservice
 * binds to port 8080 and loads an instance of the Bot class supplied as the
 * first argument. It does not bind to specific URL yet, but only switches based
 * on the last part of the requested URL. Hence running multiple bots requires
 * multiple instances of this service yet.
 *
 * @author Tobias Mahlmann
 *
 */
public class Dota2AIService extends NanoHTTPD {

	private final static ObjectMapper MAPPER = new ObjectMapper();

	private static final Logger LOGGER = Logger.getLogger(Dota2AIService.class.getName());

	private final static String ACCEPT_FIELD = "accept";

	private final static String APPLICATION_JSON = "application/json";

	private final static String CONTENT_TYPE = "content/type";

	public static void main(String[] args)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
		if (args.length < 1) {
			System.err.println("First argument must be FQN of your bot class");
			return;
		}

		final Class<Bot> botClass = (Class<Bot>) Class.forName(args[0]);
		new Dota2AIService(botClass.newInstance());

	}

	private final Bot bot;

	public Dota2AIService(Bot bot) throws IOException {
		super(8080);
		this.bot = bot;
		start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
		LOGGER.fine("Dota2AIService created");

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fi.iki.elonen.NanoHTTPD#serve(fi.iki.elonen.NanoHTTPD.IHTTPSession)
	 */
	@Override
	public Response serve(IHTTPSession session) {
		// This method does a few sanity checks and then calls the respective
		// method
		// based on the requested URL. These methods then build the response
		if (session.getMethod() != Method.POST) {
			return newFixedLengthResponse(Response.Status.METHOD_NOT_ALLOWED, "text/plain", "Only POST allowed");
		}

		final String method = session.getUri().substring(session.getUri().lastIndexOf('/') + 1);
		Response res;

		try {
			switch (method) {
			case "chat":
				res = requireJSON(session);
				if (res != null) {
					break;
				}
				chat(session);
				res = newFixedLengthResponse("");
				break;
			case "reset":
				reset(session);
				res = newFixedLengthResponse("");
				break;
			case "levelup":
				res = levelup(session);
				break;
			case "select":
				res = select(session);
				break;
			case "update":
				res = requireJSON(session);
				if (res != null) {
					break;
				}
				res = update(session);
				break;
			default:
				res = newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "method not found");
				break;

			}
		} catch (final Exception e) {
			res = newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, e.getMessage());
		}
		return res;
	}

	/**
	 * Ensures that the "accept" header is either set to JSON or empty
	 * 
	 * @param session
	 * @return
	 */
	private Response assureAcceptJSON(IHTTPSession session) {
		final Map<String, String> headers = session.getHeaders();
		if (!APPLICATION_JSON.equals(headers.get(ACCEPT_FIELD))) {
			return newFixedLengthResponse(Response.Status.NOT_ACCEPTABLE, MIME_PLAINTEXT,
					"set accept to application/json or remove it");
		} else {
			return null;
		}
	}

	/**
	 * Helper method to serialize a POJO into JSON
	 * 
	 * @param o
	 * @return
	 * @throws JsonProcessingException
	 */
	private Response buildJSONResponse(Object o) throws JsonProcessingException {
		return newFixedLengthResponse(MAPPER.writeValueAsString(o));

	}

	private void chat(IHTTPSession session) throws JsonParseException, JsonMappingException, IOException {
		final ChatEvent e = MAPPER.readValue(session.getInputStream(), ChatEvent.class);
		bot.onChat(e);
	}

	private Response levelup(IHTTPSession session) throws JsonProcessingException {
		final Response res = assureAcceptJSON(session);
		if (res != null) {
			return res;
		}
		final LevelUp l = bot.levelUp();
		return buildJSONResponse(l);
	}

	/**
	 * Ensures that the supplied data has the "content-type" set to JSON
	 * 
	 * @param session
	 * @return
	 */
	private Response requireJSON(IHTTPSession session) {
		final Map<String, String> headers = session.getHeaders();
		if (!APPLICATION_JSON.equals(headers.get(CONTENT_TYPE))) {
			return newFixedLengthResponse(Response.Status.NOT_ACCEPTABLE, MIME_PLAINTEXT,
					"Set content-type to application/json");
		} else {
			return null;
		}
	}

	private void reset(IHTTPSession session) {
		bot.reset();
	}

	private Response select(IHTTPSession session) throws JsonProcessingException {
		final Select s = bot.select();
		LOGGER.info("Select was called. We returned " + s.getHero());
		return buildJSONResponse(s);
	}

	private Response update(IHTTPSession session) throws IOException {
		final World world = MAPPER.readValue(session.getInputStream(), World.class);
		final Command c = bot.update(world);
		return buildJSONResponse(c);
	}
}
