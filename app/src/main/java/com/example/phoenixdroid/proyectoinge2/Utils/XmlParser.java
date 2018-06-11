package com.example.phoenixdroid.proyectoinge2.Utils;

import android.content.Context;

import org.osmdroid.util.GeoPoint;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class XmlParser {
    private ArrayList<PuntoEncuentro> puntosE; //Lista de los puntos seguros.
    private ArrayList<SenalVertical> senalesV; // Lista de las señales verticales.
    private BaseDeDatos bdMapa; //Base de datos que guarda información clave del mapa.
    private Context contexto;
    public List<List<GeoPoint>> rutasE = new ArrayList<>(59); //Lista de rutas de evacuación.

    public XmlParser(Context c) {
        contexto = c;
        bdMapa = new BaseDeDatos(contexto);
        parseXML();
    }

    public ArrayList<PuntoEncuentro> getPuntosE()
    {
        return puntosE;
    }
    public ArrayList<SenalVertical> getSenalesV()
    {
        return senalesV;
    }

    /**
     * Metodo para leer datos desde un archivo XML.
     */
    public void parseXML() {
        XmlPullParserFactory parserFactory;
        try {
            parserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserFactory.newPullParser();
            InputStream is = contexto.getAssets().open("puntos_encuentro.xml");
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(is, null);
            processParsingPE(parser);

            parserFactory = XmlPullParserFactory.newInstance();
            parser = parserFactory.newPullParser();
            is = contexto.getAssets().open("rutasEvacuacion.xml");
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(is, null);
            processParsingRE(parser);

            parserFactory = XmlPullParserFactory.newInstance();
            parser = parserFactory.newPullParser();
            is = contexto.getAssets().open("senalesVerticales.xml");
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(is, null);
            processParsingSV(parser);
        } catch (XmlPullParserException | IOException ignored) { }
    }

    /**
     * Lee desde un archivo XML las rutas de evacuación y  y las enlista.
     * @param parser XmlPullParser que contiene los datos leidos desde el archivo xml de rutas de evacuación.
     */
    public void processParsingRE(XmlPullParser parser) throws IOException, XmlPullParserException {
        int eventType = parser.getEventType();
        List<GeoPoint> rEActual = null;
        int id = 0;
        boolean flagID = false;
        boolean flagPunto = false;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String tag;
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    tag = parser.getName();
                    if ("way".equals(tag)) { rEActual = new ArrayList<>(); }
                    if ("id".equals(tag)) { flagID = true; }
                    if ("punto".equals(tag)) { flagPunto = true; }
                    break;

                case XmlPullParser.TEXT:
                    String text = parser.getText();
                    if (flagPunto) {
                        String[] coordenadas = text.split(" ");
                        double latitud = Double.parseDouble(coordenadas[0].substring(4));
                        double longitud = Double.parseDouble(coordenadas[1].substring(4));

                        assert rEActual != null;
                        rEActual.add(new GeoPoint(latitud, longitud));
                    } else if (flagID) {
                        id = Integer.parseInt(text.substring(1));
                    }
                    break;

                case XmlPullParser.END_TAG:
                    tag = parser.getName();
                    if (tag.equals("way")) { rutasE.add(id, rEActual); }
                    if ("id".equals(tag)) { flagID = false; }
                    if ("punto".equals(tag)) { flagPunto = false; }
                    break;
            }
            eventType = parser.next();
        }
    }

    /**
     * Lee los puntos de encuentro desde un archivo XML y los guarda en un ArrayList.
     * @param parser XmlPullParser que contiene los datos leidos desde el archivo xml de puntos de encuentro.
     */
    private void processParsingPE(XmlPullParser parser) throws IOException, XmlPullParserException {
        puntosE = new ArrayList<>();
        int eventType = parser.getEventType();
        PuntoEncuentro puntoEActual = null;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String tag;

            switch(eventType){
                case XmlPullParser.START_TAG:
                    tag = parser.getName();

                    if ("node".equals(tag)) {
                        puntoEActual = new PuntoEncuentro();
                        puntosE.add(puntoEActual);

                    } else {
                        if ("nombre".equals(tag)){
                            puntoEActual.nombre = parser.nextText();
                        } else if ("lat".equals(tag)) {
                            puntoEActual.latitud = Double.parseDouble(parser.nextText());
                        } else if ("lon".equals(tag)) {
                            puntoEActual.longitud = Double.parseDouble(parser.nextText());
                            bdMapa.agregarPuntoSeguro(puntoEActual.latitud, puntoEActual.longitud, puntoEActual.nombre);
                        }

                    }
                    break;
            }
            eventType = parser.next();
        }
    }

    /**
     * Lee las señales verticales desde un archivo XML y los guarda en un ArrayList.
     * @param parser XmlPullParser que contiene los datos leidos desde el archivo xml de puntos de encuentro.
     */
    private void processParsingSV(XmlPullParser parser) throws IOException, XmlPullParserException {
        senalesV = new ArrayList<>();
        int eventType = parser.getEventType();
        SenalVertical senalVActual = null;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String tag;
            switch(eventType){
                case XmlPullParser.START_TAG:
                    tag = parser.getName();

                    if ("node".equals(tag)) {
                        senalVActual = new SenalVertical();
                        senalesV.add(senalVActual);
                    } else if (senalVActual != null) {
                        if ("num".equals(tag)){
                            senalVActual.id = Integer.parseInt(parser.nextText());
                        } else if ("lat".equals(tag)) {
                            senalVActual.latSV = Double.parseDouble(parser.nextText());
                        } else if ("lon".equals(tag)) {
                            senalVActual.lonSV = Double.parseDouble(parser.nextText());
                        } else if ("lado".equals(tag)){
                            senalVActual.lado = parser.nextText();
                        }
                    }
                    break;
            }
            eventType = parser.next();
        }
    }
}
