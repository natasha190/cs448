package heap;

import java.util.Iterator;

import chainexception.ChainException;
import global.Minibase;
import global.Page;
import global.PageId;
import global.RID;

public class HeapScan {

	/**
	 * 
	 * Constructs a file scan by pinning the directoy header page and
	 * initializing
	 * 
	 * iterator fields.
	 */

	private HeapFile hf;
	private HFPage current;

	private RID crid;
	private Iterator<PageId> it;
	private boolean init;

	protected HeapScan(HeapFile hf) {

		init = true;
		this.hf = hf;
		it = hf.iterator();
		PageId currentId = it.next();
		Page page=new Page();
		Minibase.BufferManager.pinPage(currentId, page, false);

		current = new HFPage(page);
		crid = current.firstRecord();

	}

	protected void finalize() throws Throwable {
		// if(current != null)

		// Minibase.BufferManager.unpinPage(current.getCurPage(), false);

		current = null;

		crid = null;
		it = null;

	}

	/**
	 * 
	 * Closes the file scan, releasing any pinned pages.
	 */
	public void close() throws ChainException // TODO
	{

		try {
			finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		hf = null;

	}

	/**
	 * 
	 * Returns true if there are more records to scan, false otherwise.
	 */
	public boolean hasNext() {
		return it.hasNext();

	}

	/**
	 * 
	 * Gets the next record in the file scan.
	 * 
	 * @param rid
	 *            output parameter that identifies the returned record
	 * 
	 * @throws IllegalStateException
	 *             if the scan has no more elements
	 */
	public Tuple getNext(RID rid) throws IllegalStateException {
		//if(hf.getRecCnt() == 0)
			//throw new IllegalStateException();
		if (crid == null) {
			if (it.hasNext()) {
				Minibase.BufferManager.unpinPage(current.getCurPage(), false);
				PageId temp = it.next();

				Minibase.BufferManager.pinPage(temp, current, false);
				crid = current.firstRecord();

				if (crid == null)
					Minibase.BufferManager.unpinPage(temp, false);
			} else {
				Minibase.BufferManager.unpinPage(current.getCurPage(), false);
			}
		}
		if (crid != null) {
			rid.copyRID(crid);
			crid = current.nextRecord(crid);
			return new Tuple(current.selectRecord(rid));
			
		}
		
	
		return null;

	}

}
