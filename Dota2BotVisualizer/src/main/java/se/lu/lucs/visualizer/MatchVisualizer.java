package se.lu.lucs.visualizer;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import de.lighti.components.map.MapCanvasComponent;
import de.lighti.components.map.data.XYZSeries;
import de.lighti.components.map.resources.MinimapIcons;
import de.lighti.components.map.resources.MinimapIcons.ICON_FLAVOUR;
import se.lu.lucs.dota2.framework.bot.Bot.Command;
import se.lu.lucs.dota2.framework.game.BaseEntity;
import se.lu.lucs.dota2.framework.game.BaseNPC;
import se.lu.lucs.dota2.framework.game.Building;
import se.lu.lucs.dota2.framework.game.Hero;
import se.lu.lucs.dota2.framework.game.World;
import se.lu.lucs.dota2.service.FrameListener;

public class MatchVisualizer extends JFrame implements FrameListener {
    private final static int CELL_WIDTH = 128;
    private final static int CELL_ORIGIN = 128;
    private final static int GARBAGE_COLLECTOR_INTERVAL = 10000;

    private static Image createImage( String name, boolean radiant, ICON_FLAVOUR f ) {
        return MinimapIcons.getMinimapIcon( "CDOTA_UNIT_HERO_" + name.replace( "npc_dota_hero", "" ).replace( "_", "" ).toUpperCase(), radiant, f );
    }

    private static XYZSeries createSeriesForUnit( BaseNPC e, String id ) {
        final String name = e.getName();
        final XYZSeries s = new XYZSeries( name + "_" + id );

        if (e instanceof Hero || e instanceof Building) {
            s.setUseImages( true );
            s.setDefaultImage( createImage( name, e.getTeam() == 2, ICON_FLAVOUR.NORMAL ) );
        }
        else {
            s.setUseImages( false );
            s.setSeriesColor( e.getTeam() == 2 ? Color.GREEN : Color.RED );
        }
        return s;
    }

    private final MapCanvasComponent mapCanvas;

    private final Map<String, XYZSeries> unitSeries;
    private final Timer timer;

    public MatchVisualizer() {
        unitSeries = new HashMap<>();

        mapCanvas = new MapCanvasComponent();

        add( mapCanvas );
        addWindowListener( new WindowAdapter() {

            @Override
            public void windowClosing( WindowEvent e ) {
                timer.stop();

                System.out.println( "Closed" );
                System.exit( 0 );
            }
        } );
        addKeyListener( new KeyAdapter() {

            @Override
            public void keyPressed( KeyEvent ev ) {
                switch (ev.getKeyChar()) {
                    case 'd':
                        for (final Map.Entry<String, XYZSeries> e : unitSeries.entrySet()) {
                            System.out.println( e.getKey() );
                            final XYZSeries.XYZDataItem i = e.getValue().getByZ( 0l );
                            System.out.println( i.x + ", " + i.y );
                        }
                        break;
                    default:
                        break;
                }
            }

        } );
        setResizable( false );
        pack();
        setVisible( true );

        timer = new Timer( GARBAGE_COLLECTOR_INTERVAL, ( e ) -> {
            if (unitSeries.isEmpty()) {
                return;
            }

            final Set<String> removeKeys = new HashSet();
            for (final Map.Entry<String, XYZSeries> en : unitSeries.entrySet()) {
                if (!mapCanvas.hasSeries( en.getValue() )) {
                    removeKeys.add( en.getKey() );
                }
            }
            System.out.println( removeKeys.size() + " obselete series removed" );

            unitSeries.keySet().removeAll( removeKeys );

        } );
        timer.setInitialDelay( GARBAGE_COLLECTOR_INTERVAL );
        timer.start();
    }

    private XYZSeries getSeriesFor( int id, BaseNPC e ) {
        final String name = Integer.toString( id );

        XYZSeries ret = unitSeries.get( name );
        if (ret == null) {
            ret = createSeriesForUnit( e, name );
            unitSeries.put( name, ret );
            mapCanvas.addSeries( ret );
        }
        if (!e.isAlive()) {
            ret.getByZ( 0l ).image = createImage( name, e.getTeam() == 2, ICON_FLAVOUR.DEAD );
        }
        else if (e.isRooted()) {
            ret.getByZ( 0l ).image = createImage( name, e.getTeam() == 2, ICON_FLAVOUR.STUNNED );
        }
        return ret;

    }

    @Override
    public Command update( World w ) {
        //Send this to the swing worker so we don't create concurrency issues with rendering
        SwingUtilities.invokeLater( () -> {
            mapCanvas.startUpdate();
            unitSeries.values().forEach( s -> mapCanvas.removeSeries( s ) );
            for (final Map.Entry<Integer, BaseEntity> e : w.getEntities().entrySet()) {
                if (e.getValue() instanceof BaseNPC) {
                    final XYZSeries s = getSeriesFor( e.getKey(), (BaseNPC) e.getValue() );
                    final float[] origin = e.getValue().getOrigin();
                    s.removeAll();
                    s.add( CELL_ORIGIN + (int) origin[0] / CELL_WIDTH, CELL_ORIGIN + (int) origin[1] / CELL_WIDTH, 0l );
                    mapCanvas.addSeries( s );
                }
                else {
                    System.out.println( e + " is not an npc" );
                }
            }

            mapCanvas.endUpdate();
        } );
        return null;
    }

}
