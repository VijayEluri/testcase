/*
 * CStringList.java
 *
 * Created on October 8, 2004, 12:42 PM
 */

package testcase; 

import java.util.Vector;

/** Encapsulation of a list of strings with associated objects.  The concept 
     behind this class is loosely based on the TStringList class in the 
     Delphi library. */
public class CStringList
{
   /** list of associated strings */
   protected Vector mystrings;
   /** list of associated objects */
   protected Vector myobjects;
   /** whether list is sorted by string */
   protected boolean issorted;
   /** whether index returned by findIndex was actual index or insertion point. */
   protected boolean wasfound;
   
   /** Creates a new instance of CStringList that is not a sorted list. */
   public CStringList()
   {
      mystrings = new Vector(16,16);
      myobjects = new Vector(16,16);
      issorted = false;
      wasfound = false;
   }

   /** Creates a new instance of CStringList where the referenced parameter indicates whether the
       list is sorted.
       @param asorted whether or not the list is to be sorted. */
   public CStringList(boolean asorted)
   {
      mystrings = new Vector(16,16);
      myobjects = new Vector(16,16);
      issorted = asorted;
      wasfound = false;
   }

   /** Add referenced item to the list.
       @param astr associated string. 
       @param aobj associated object. */
   public synchronized void addItem(String astr, Object aobj)
   {
      int idx;
      if (issorted)
      {
         idx = findIndex(astr);
         if (wasfound)
         {
            myobjects.setElementAt(aobj, idx);
         }
         else
         {
            mystrings.insertElementAt(astr, idx);
            myobjects.insertElementAt(aobj, idx);
         }
      }
      else
      {
         idx = getIndex(astr);
         if (idx < 0)
         {
            mystrings.addElement(astr);
            myobjects.addElement(aobj);
         }
         else
         {
            myobjects.setElementAt(aobj, idx);
         }
      }
   }
   
   /** Delete item at referenced index from the list. 
       @param aidx index of list item. */
   public void delItem(int aidx)
   {
      if (aidx >= 0 && aidx < getCount())
      {
         mystrings.removeElementAt(aidx);
         myobjects.removeElementAt(aidx);
      }
   }
   
   /** Delete item associated with referenced string from the list. 
       @param astr associated string. */
   public void delItem(String astr)
   {
      int idx = getIndex(astr);
      if (idx >= 0) delItem(idx);
   }
   
   /** Set the object at referenced index to referenced object. 
       @param aidx index of list item. 
       @param aobj associated object. */
   public void setObject(int aidx, Object aobj)
   {
      if (aidx >= 0 && aidx < getCount())
         myobjects.setElementAt(aobj, aidx);
   }

   /** Set the object associated with referenced string to referenced object. 
       Add item to list if necessary. 
       @param astr associated string. 
       @param aobj associated object. */
   public void setItem(String astr, Object aobj)
   {
      addItem(astr, aobj);
   }
   
   /** Get the object at referenced index. 
       @param aidx index of list item. 
       @return the associated object. */
   public Object getItem(int aidx)
   {
      if (aidx >= 0 && aidx < getCount())
         return myobjects.elementAt(aidx);
      else
         return(null);
   }

   /** Get the string at referenced index. 
       @param aidx index of list item.
       @return the associated string. */
   public String getString(int aidx)
   {
      if (aidx >= 0 && aidx < getCount())
        return mystrings.elementAt(aidx).toString();
      else
         return("");
   }

   /** Get the object associated with the referenced string. 
       @param astr associated string. 
       @return the associated object. */
   public Object getObject(String astr)
   {
      int idx = getIndex(astr);
      if (idx < 0) return(null);
      return(getItem(idx));
   }
   
   /** Clear the list. */
   public void clear()
   {
      mystrings.removeAllElements();
      myobjects.removeAllElements();
   }
   
   /** Find the index of the referenced string.  If the string is not in the list,
       then return the index where the item would be inserted.  Set wasfound to
       indicate whether item is in the list. 
       @param astr associated string. 
       @return the index of list item or the insertion index.*/
   protected int findIndex(String astr)
   {
      int ltop;
      int lbot;
      int lcur;
      int lcmp;
      
      wasfound = false;
      ltop = 0;
      lbot = mystrings.size() - 1;
      while (ltop <= lbot)
      {
         lcur = (ltop + lbot) / 2;
         lcmp = astr.compareTo(mystrings.elementAt(lcur).toString());
         if (lcmp > 0)
            ltop = lcur + 1;
         else
         {
            lbot = lcur - 1;
            if (lcmp == 0)
            {
               wasfound = true;
               ltop = lcur;
            }
         }
      }
      return(ltop);
   }
   
   /** Get the index of the referenced string.  
       @param astr associated string. 
       @return the index of list item or -1 if not in list.*/
   public synchronized int getIndex(String astr)
   {
      int idx; 
      int ndx;
      if (issorted)
      {
         idx = findIndex(astr);
         if (wasfound) return(idx);
         return(-1);
      }
      ndx = getCount();
      for (idx = 0; idx < ndx; idx++)
         if (astr.compareTo(mystrings.elementAt(idx).toString()) == 0) return(idx);
      return(-1);
   }
   
   /** Get number of items in the list. 
       @return the number of items in the list. */
   public int getCount()
   {
      return(mystrings.size());
   }
   
   /** Copy the referenced list. 
       @param alist list to copy. */
   public void copyList(CStringList alist)
   {
      this.clear();
      for (int idx = 0; idx < alist.getCount(); idx++)
      {
         this.addItem(alist.getString(idx), alist.getItem(idx));
      }
   }
   
   public static String padInt(int aval, int alen, String apad)
   {
      String buf = Integer.toString(aval);
      while (buf.length() < alen)
         buf = apad + buf;
      return(buf);
   }

   public String makeNewId(String aprefix, int alen)
   {
      return(makeNewId("", aprefix, alen));
   }

   public String makeNewId(String akeyfix, String aprefix, int alen)
   {
      String mykey = "";
      if (akeyfix.length() > 0) mykey = akeyfix + "|";
      int padlen = alen = aprefix.length();
      int num = 0;
      while (true)
      {
         num++;
         String myid = aprefix + CStringList.padInt(num, padlen, "0");
         if (getIndex(mykey+myid) == -1) return(myid);
      }
   }
}
