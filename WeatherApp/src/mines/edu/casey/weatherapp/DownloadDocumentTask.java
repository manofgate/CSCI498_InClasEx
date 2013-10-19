package mines.edu.casey.weatherapp;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Uses AsyncTask to create a task away from the main UI thread.
 * 
 * This task takes a URL string and uses it to create an HttpUrlConnection. Once the connection has been established,
 * the AsyncTask downloads the contents of the web page as an InputStream. Finally, the InputStream is converted into
 * a Document, which is returned to the class that instantiated this task via the receiveResult callback.
 * 
 * The code in this demo is adapted from the following:
 *   http://developer.android.com/training/basics/network-ops/connecting.html
 *   http://developer.android.com/reference/android/os/AsyncTask.html
 * 
 * @author Randy Bower
 */
public class DownloadDocumentTask extends AsyncTask<String, Void, Document>
{
  private Listener listener;

  public interface Listener
  {
    public void receiveResult( Document result );
  }

  public DownloadDocumentTask( Listener listener )
  {
    super();
    this.listener = listener;
  }

  @Override
  protected Document doInBackground( String... urls )
  {
    // urls comes from the execute() call: urls[ 0 ] is the url to download.
    try
    {
      return downloadUrl( urls[0] );
    }
    catch( IOException e )
    {
      Log.e( "INTERNET ACCESS DEMO", "Unable to retrieve web page. URL may be invalid." );
      return null;
    }
  }

  // onPostExecute receives the results of the AsyncTask on the UI thread.
  @Override
  protected void onPostExecute( Document result )
  {
    this.listener.receiveResult( result );
  }

  // Given a URL, establishes an HttpUrlConnection and retrieves the web page content as a InputStream.
  // The contents of the stream are then returned as a Document.
  private Document downloadUrl( String myurl ) throws IOException
  {
    InputStream input = null;
    try
    {
      URL url = new URL( myurl );
      HttpURLConnection conn = (HttpURLConnection)url.openConnection();
      conn.setReadTimeout( 10000 );  // milliseconds
      conn.setConnectTimeout( 15000 );  // milliseconds
      conn.setRequestMethod( "GET" );
      conn.setDoInput( true );

      // Starts the query
      conn.connect();
      int response = conn.getResponseCode();
      Log.d( "INTERNET ACCESS DEMO", "The response is: " + response );
      input = conn.getInputStream();

      // Convert the InputStream into a Document
      return readDocument( input );
    }
    // Makes sure that the InputStream is closed after the app is finished using it.
    finally
    {
      if( input != null )
      {
        input.close();
      }
    }
  }

  // Reads an InputStream and converts it to a Document.
  private Document readDocument( InputStream stream ) throws IOException, UnsupportedEncodingException
  {
    try
    {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      return db.parse( stream );  // This returns a Document object.
    }
    catch( ParserConfigurationException e )
    {
      e.printStackTrace();
    }
    catch( SAXException e )
    {
      e.printStackTrace();
    }

    // In case the try section above fails to return a valid Document.
    return null;
  }
}
