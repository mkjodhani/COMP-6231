package org.omg.CORBA;


/**
* org/omg/CORBA/CustomerHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from DMTBS.idl
* Tuesday, 21 February, 2023 9:31:14 AM EST
*/

abstract public class CustomerHelper
{
  private static String  _id = "IDL:CORBA/Customer:1.0";

  public static void insert (org.omg.CORBA.Any a, org.omg.CORBA.Customer that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static org.omg.CORBA.Customer extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.CORBA.ORB.init ().create_interface_tc (org.omg.CORBA.CustomerHelper.id (), "Customer");
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static org.omg.CORBA.Customer read (org.omg.CORBA.portable.InputStream istream)
  {
    return narrow (istream.read_Object (_CustomerStub.class));
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, org.omg.CORBA.Customer value)
  {
    ostream.write_Object ((org.omg.CORBA.Object) value);
  }

  public static org.omg.CORBA.Customer narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof org.omg.CORBA.Customer)
      return (org.omg.CORBA.Customer)obj;
    else if (!obj._is_a (id ()))
      throw new org.omg.CORBA.BAD_PARAM ();
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      org.omg.CORBA._CustomerStub stub = new org.omg.CORBA._CustomerStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

  public static org.omg.CORBA.Customer unchecked_narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof org.omg.CORBA.Customer)
      return (org.omg.CORBA.Customer)obj;
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      org.omg.CORBA._CustomerStub stub = new org.omg.CORBA._CustomerStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

}
