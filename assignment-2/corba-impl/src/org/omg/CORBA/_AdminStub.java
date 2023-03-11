package org.omg.CORBA;


/**
* org/omg/CORBA/_AdminStub.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from DMTBS.idl
* Tuesday, 21 February, 2023 9:31:14 AM EST
*/

public class _AdminStub extends org.omg.CORBA.portable.ObjectImpl implements org.omg.CORBA.Admin
{

  public String addMovieSlots (String movieID, String movieName, int bookingCapacity)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("addMovieSlots", true);
                $out.write_wstring (movieID);
                $out.write_wstring (movieName);
                $out.write_long (bookingCapacity);
                $in = _invoke ($out);
                String $result = $in.read_wstring ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return addMovieSlots (movieID, movieName, bookingCapacity        );
            } finally {
                _releaseReply ($in);
            }
  } // addMovieSlots

  public String removeMovieSlots (String movieID, String movieName)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("removeMovieSlots", true);
                $out.write_wstring (movieID);
                $out.write_wstring (movieName);
                $in = _invoke ($out);
                String $result = $in.read_wstring ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return removeMovieSlots (movieID, movieName        );
            } finally {
                _releaseReply ($in);
            }
  } // removeMovieSlots

  public String listMovieShowsAvailability (String movieName)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("listMovieShowsAvailability", true);
                $out.write_wstring (movieName);
                $in = _invoke ($out);
                String $result = $in.read_wstring ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return listMovieShowsAvailability (movieName        );
            } finally {
                _releaseReply ($in);
            }
  } // listMovieShowsAvailability

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:CORBA/Admin:1.0"};

  public String[] _ids ()
  {
    return (String[])__ids.clone ();
  }

  private void readObject (java.io.ObjectInputStream s) throws java.io.IOException
  {
     String str = s.readUTF ();
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init (args, props);
   try {
     org.omg.CORBA.Object obj = orb.string_to_object (str);
     org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl) obj)._get_delegate ();
     _set_delegate (delegate);
   } finally {
     orb.destroy() ;
   }
  }

  private void writeObject (java.io.ObjectOutputStream s) throws java.io.IOException
  {
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init (args, props);
   try {
     String str = orb.object_to_string (this);
     s.writeUTF (str);
   } finally {
     orb.destroy() ;
   }
  }
} // class _AdminStub
