package de.lighti.dota2.service;

import java.util.logging.Logger;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.lighti.dota2.bot.BotCommands.LevelUp;
import de.lighti.dota2.bot.BotCommands.Select;
import de.lighti.dota2.bot.Lina;
import de.lighti.dota2.game.Bot;
import de.lighti.dota2.game.Bot.Command;
import de.lighti.dota2.game.ChatEvent;
import de.lighti.dota2.game.World;

@Path( "/service" )
@Singleton
public class Dota2AIService {
    private static final Logger LOGGER = Logger.getLogger( Dota2AIService.class.getName() );
    private final Bot bot = new Lina();

    public Dota2AIService() {
        LOGGER.fine( "Dota2AIService created" );
    }

    @POST
    @Path( "/chat" )
    @Consumes( MediaType.APPLICATION_JSON )
    public Response chat( ChatEvent e ) {
        bot.onChat( e );
        return Response.status( Status.OK ).build();
    }

    @POST
    @Path( "/select" )
    @Produces( MediaType.APPLICATION_JSON )
    public Response select( Select e ) {
        final Select s = bot.select();
        LOGGER.info( "Select was called. We returned " + s.getHero() );
        return Response.status( Status.OK ).entity( s ).build();
    }

    @POST
    @Path( "/levelup" )
    @Produces( MediaType.APPLICATION_JSON )
    public Response levelUp() {
        final LevelUp up = bot.levelUp();
        return Response.status( Status.OK ).entity( up ).build();
    }

    @POST
    @Path( "/reset" )
    @Consumes( MediaType.APPLICATION_JSON )
    public Response reset( String input ) {
        bot.reset();
        return Response.status( Status.OK ).build();
    }

    @POST
    @Path( "/test" )
    @Consumes( MediaType.APPLICATION_JSON )
    public Response test( String input ) {
        System.out.println( input );
        return Response.status( Status.OK ).build();
    }

    @POST
    @Path( "/update" )
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( MediaType.APPLICATION_JSON )
    public Response update( World world ) {
        try {
            final Command command = bot.update( world );

            // return HTTP response 200 in case of success
            return Response.status( Status.OK ).entity( command ).build();
        }
        catch (final Exception e) {
            System.out.println( world.toString() );

            throw e;
        }
    }

}
