/* ###
 * IP: GHIDRA
 * REVIEWED: YES
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ghidra.app.plugin.core.select.reference;

import ghidra.app.context.ListingContextAction;
import ghidra.app.context.ListingActionContext;
import ghidra.app.nav.NavigationUtils;
import ghidra.app.util.HelpTopics;
import ghidra.framework.plugintool.PluginTool;
import ghidra.program.model.address.*;
import ghidra.program.model.listing.*;
import ghidra.program.model.symbol.Reference;
import ghidra.program.util.ProgramSelection;
import ghidra.util.HelpLocation;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import docking.action.KeyBindingData;
import docking.action.MenuData;

public class SelectForwardRefsAction extends ListingContextAction {

	private final PluginTool tool;

	SelectForwardRefsAction(PluginTool tool, String owner) {
		super("Forward Refs", owner);
		this.tool = tool;

		String group = "references";
		setMenuBarData( new MenuData( new String[] {"Select", "Forward Refs"}, null, group ) );
		
		setKeyBindingData( new KeyBindingData( KeyEvent.VK_PERIOD, InputEvent.CTRL_MASK ) ); 
		setHelpLocation(new HelpLocation(HelpTopics.SELECTION, "Forward"));
//		setKeyBindingData( new KeyBindingData(KeyEvent.VK_SEMICOLON, InputEvent.CTRL_MASK ) );
//		setHelpLocation(new HelpLocation(HelpTopics.SELECTION, "Backward"));
	}
	
	@Override
	protected boolean isEnabledForContext(ListingActionContext context) {
		return context.getAddress() != null || context.hasSelection();
	}
	/**
	 * Method called when the action is invoked.
	 * @param ActionEvent details regarding the invocation of this action
	 */
	@Override
    public void actionPerformed(ListingActionContext context) {
		
		AddressSetView addressSet = context.hasSelection() ? 
				context.getSelection() :
				new AddressSet(context.getAddress());
				
		ProgramSelection selection = getSelection(context.getProgram(), addressSet);
		NavigationUtils.setSelection(tool, context.getNavigatable(), selection);
	}

	private ProgramSelection getSelection(Program program, AddressSetView addressSetView){
		AddressSet addressSet = new AddressSet();

		CodeUnitIterator iter = program.getListing().getCodeUnits(addressSetView,true);

		while (iter.hasNext()){
			CodeUnit cu=iter.next();
			Reference[] memRef=cu.getReferencesFrom();
			for (int i=0;i<memRef.length;i++){
				Address addr = memRef[i].getToAddress();
				if ( addr.isMemoryAddress() ) {
				    addressSet.addRange(addr,addr);
				}
									
			}
		}
		return new ProgramSelection(addressSet);
	}
}
