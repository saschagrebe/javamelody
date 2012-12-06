/*
 * Copyright 2008-2012 by Emeric Vernat
 *
 *     This file is part of Java Melody.
 *
 * Java Melody is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Java Melody is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Java Melody.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.bull.javamelody;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.MatteBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import net.bull.javamelody.swing.MButton;
import net.bull.javamelody.swing.Utilities;
import net.bull.javamelody.swing.table.MDefaultTableCellRenderer;
import net.bull.javamelody.swing.table.MTable;
import net.bull.javamelody.swing.table.MTableScrollPane;

/**
 * Panel du tableau des requêtes courantes.
 * @author Emeric Vernat
 */
class CounterRequestContextPanel extends CounterRequestAbstractPanel {
	private static final long serialVersionUID = 1L;

	private final CounterRequestContextData data;

	private final JPanel buttonsPanel;

	private abstract class CounterRequestTableCellRenderer extends MDefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;
		private final MatteBorder contextBorder = BorderFactory.createMatteBorder(1, 0, 0, 0,
				Color.GRAY);

		CounterRequestTableCellRenderer() {
			super();
		}

		protected CounterRequest getCounterRequest(int row) {
			final int modelRow = getTable().convertRowIndexToModel(row);
			return getData().getAllRequests().get(modelRow);
		}

		protected CounterRequestContext getCounterRequestContext(int row) {
			final int modelRow = getTable().convertRowIndexToModel(row);
			return getData().getAllContexts().get(modelRow);
		}

		int getParentContextLevel(int row) {
			CounterRequestContext counterRequestContext = getCounterRequestContext(row);
			int level = 0;
			while (counterRequestContext.getParentContext() != null) {
				level++;
				counterRequestContext = counterRequestContext.getParentContext();
			}
			return level;
		}

		@Override
		public Component getTableCellRendererComponent(JTable jtable, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			final Component result = super.getTableCellRendererComponent(jtable, value, isSelected,
					hasFocus, row, column);
			if (row != 0 && getParentContextLevel(row) == 0) {
				setBorder(BorderFactory.createCompoundBorder(contextBorder, getBorder()));
			}
			return result;
		}
	}

	private final class ThreadTableCellRenderer extends CounterRequestTableCellRenderer {
		private static final long serialVersionUID = 1L;

		ThreadTableCellRenderer() {
			super();
		}

		@Override
		public Component getTableCellRendererComponent(JTable jtable, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			final CounterRequestContext counterRequestContext = getCounterRequestContext(row);
			final ThreadInformations threadInformations = getData()
					.getThreadInformationsByCounterRequestContext(counterRequestContext);
			final String threadName;
			if (threadInformations == null) {
				threadName = null; // un décalage n'a pas permis de récupérer le thread de ce context
				setToolTipText(null);
			} else {
				threadName = threadInformations.getName();
				setToolTipText(ThreadInformationsPanel.convertStackTraceToHtml(
						threadInformations.getName(), threadInformations.getStackTrace()));
			}
			return super.getTableCellRendererComponent(jtable, threadName, isSelected, hasFocus,
					row, column);
		}
	}

	private final class ExecutedMethodTableCellRenderer extends CounterRequestTableCellRenderer {
		private static final long serialVersionUID = 1L;

		ExecutedMethodTableCellRenderer() {
			super();
		}

		@Override
		public Component getTableCellRendererComponent(JTable jtable, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			final CounterRequestContext counterRequestContext = getCounterRequestContext(row);
			final ThreadInformations threadInformations = getData()
					.getThreadInformationsByCounterRequestContext(counterRequestContext);
			final String executedMethod;
			if (threadInformations == null) {
				executedMethod = null; // un décalage n'a pas permis de récupérer le thread de ce context
			} else {
				executedMethod = threadInformations.getExecutedMethod();
			}

			return super.getTableCellRendererComponent(jtable, executedMethod, isSelected,
					hasFocus, row, column);
		}
	}

	private final class RemoteUserTableCellRenderer extends CounterRequestTableCellRenderer {
		private static final long serialVersionUID = 1L;

		RemoteUserTableCellRenderer() {
			super();
		}

		@Override
		public Component getTableCellRendererComponent(JTable jtable, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			final CounterRequestContext counterRequestContext = getCounterRequestContext(row);
			final String remoteUser = counterRequestContext.getRemoteUser();
			return super.getTableCellRendererComponent(jtable, remoteUser, isSelected, hasFocus,
					row, column);
		}
	}

	private final class NameTableCellRenderer extends CounterRequestTableCellRenderer {
		private static final long serialVersionUID = 1L;

		NameTableCellRenderer() {
			super();
		}

		@Override
		public Component getTableCellRendererComponent(JTable jtable, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			final CounterRequest counterRequest = getCounterRequest(row);
			final int margin = 10 * getParentContextLevel(row);
			final Counter counter = getCounterByRequestId(counterRequest);
			setIcon(getCounterIcon(counter, margin));
			return super.getTableCellRendererComponent(jtable, value, isSelected, hasFocus, row,
					column);
		}
	}

	private class IntegerTableCellRenderer extends CounterRequestTableCellRenderer {
		private static final long serialVersionUID = 1L;

		private final Integer minusOne = Integer.valueOf(-1);
		private final DecimalFormat integerFormat = I18N.createIntegerFormat();

		IntegerTableCellRenderer() {
			super();
			setHorizontalAlignment(RIGHT);
		}

		@Override
		public void setValue(Object value) {
			final String text;
			if (value != null && !minusOne.equals(value)) {
				text = integerFormat.format(value);
			} else {
				text = null;
			}
			super.setValue(text);
		}
	}

	private final class DurationTableCellRenderer extends IntegerTableCellRenderer {
		private static final long serialVersionUID = 1L;

		DurationTableCellRenderer() {
			super();
		}

		@Override
		public Component getTableCellRendererComponent(JTable jtable, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			final CounterRequest counterRequest = getCounterRequest(row);
			final int margin = 10 * getParentContextLevel(row);
			final Counter counter = getCounterByRequestId(counterRequest);
			setIcon(getCounterIcon(counter, margin));
			final CounterRequestContext counterRequestContext = getCounterRequestContext(row);
			final int duration = counterRequestContext.getDuration(counterRequestContext
					.getParentCounter().getStartDate().getTime());
			final CounterRequestAggregation aggregation = getData().getAggregationForCounter(
					counter);
			final Component result = super.getTableCellRendererComponent(jtable, duration,
					isSelected, hasFocus, row, column);
			StatisticsTablePanel.setStyleBasedOnThresholds(this, duration, aggregation);
			return result;
		}
	}

	private final class CpuTableCellRenderer extends IntegerTableCellRenderer {
		private static final long serialVersionUID = 1L;

		CpuTableCellRenderer() {
			super();
		}

		@Override
		public Component getTableCellRendererComponent(JTable jtable, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			final Integer cpu;
			final CounterRequest counterRequest = getCounterRequest(row);
			if (counterRequest.getCpuTimeMean() >= 0) {
				final CounterRequestContext counterRequestContext = getCounterRequestContext(row);
				cpu = counterRequestContext.getCpuTime();
			} else {
				cpu = null;
			}
			return super.getTableCellRendererComponent(jtable, cpu, isSelected, hasFocus, row,
					column);
		}
	}

	private final class TotalChildHitsTableCellRenderer extends IntegerTableCellRenderer {
		private static final long serialVersionUID = 1L;

		TotalChildHitsTableCellRenderer() {
			super();
		}

		@Override
		public Component getTableCellRendererComponent(JTable jtable, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			final Integer totalChildHits;
			final CounterRequestContext context = getCounterRequestContext(row);
			if (context.getParentCounter().getChildCounterName() == null) {
				// si le compteur parent du contexte n'a pas de compteur fils
				// (comme le compteur sql et au contraire du compteur http),
				// alors la valeur de childHits ou childDurations n'a pas de sens
				// (comme les hits sql fils pour une requête sql)
				totalChildHits = -1;
			} else {
				totalChildHits = context.getTotalChildHits();
			}
			return super.getTableCellRendererComponent(jtable, totalChildHits, isSelected,
					hasFocus, row, column);
		}
	}

	private final class ChildHitsMeanTableCellRenderer extends IntegerTableCellRenderer {
		private static final long serialVersionUID = 1L;

		ChildHitsMeanTableCellRenderer() {
			super();
		}

		@Override
		public Component getTableCellRendererComponent(JTable jtable, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			final Integer childHitsMean;
			final CounterRequestContext context = getCounterRequestContext(row);
			if (context.getParentCounter().getChildCounterName() == null) {
				// si le compteur parent du contexte n'a pas de compteur fils
				// (comme le compteur sql et au contraire du compteur http),
				// alors la valeur de childHits ou childDurations n'a pas de sens
				// (comme les hits sql fils pour une requête sql)
				childHitsMean = -1;
			} else {
				final CounterRequest counterRequest = getCounterRequest(row);
				childHitsMean = counterRequest.getChildHitsMean();
			}
			return super.getTableCellRendererComponent(jtable, childHitsMean, isSelected, hasFocus,
					row, column);
		}
	}

	private final class TotalChildDurationsSumTableCellRenderer extends IntegerTableCellRenderer {
		private static final long serialVersionUID = 1L;

		TotalChildDurationsSumTableCellRenderer() {
			super();
		}

		@Override
		public Component getTableCellRendererComponent(JTable jtable, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			final Integer totalChildDurationsSum;
			final CounterRequestContext context = getCounterRequestContext(row);
			if (context.getParentCounter().getChildCounterName() == null) {
				// si le compteur parent du contexte n'a pas de compteur fils
				// (comme le compteur sql et au contraire du compteur http),
				// alors la valeur de childHits ou childDurations n'a pas de sens
				// (comme les hits sql fils pour une requête sql)
				totalChildDurationsSum = -1;
			} else {
				totalChildDurationsSum = context.getTotalChildDurationsSum();
			}
			return super.getTableCellRendererComponent(jtable, totalChildDurationsSum, isSelected,
					hasFocus, row, column);
		}
	}

	private final class ChildsDurationsMeanTableCellRenderer extends IntegerTableCellRenderer {
		private static final long serialVersionUID = 1L;

		ChildsDurationsMeanTableCellRenderer() {
			super();
		}

		@Override
		public Component getTableCellRendererComponent(JTable jtable, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			final Integer childHitsMean;
			final CounterRequestContext context = getCounterRequestContext(row);
			if (context.getParentCounter().getChildCounterName() == null) {
				// si le compteur parent du contexte n'a pas de compteur fils
				// (comme le compteur sql et au contraire du compteur http),
				// alors la valeur de childHits ou childDurations n'a pas de sens
				// (comme les hits sql fils pour une requête sql)
				childHitsMean = -1;
			} else {
				final CounterRequest counterRequest = getCounterRequest(row);
				childHitsMean = counterRequest.getChildDurationsMean();
			}
			return super.getTableCellRendererComponent(jtable, childHitsMean, isSelected, hasFocus,
					row, column);
		}
	}

	CounterRequestContextPanel(RemoteCollector remoteCollector,
			List<CounterRequestContext> currentRequests, JavaInformations javaInformations) {
		super(remoteCollector);
		this.data = new CounterRequestContextData(getCounters(), currentRequests, javaInformations);

		final MTableScrollPane<CounterRequest> scrollPane = createScrollPane();

		getTable().setList(data.getAllRequests());
		Utilities.adjustTableHeight(getTable());
		add(scrollPane, BorderLayout.CENTER);

		this.buttonsPanel = createButtonsPanel(false);
		add(buttonsPanel, BorderLayout.SOUTH);
	}

	CounterRequestContextPanel createDetailsPanel(List<CounterRequestContext> currentRequests,
			MButton detailsButton) {
		final CounterRequestContextPanel detailsPanel = new CounterRequestContextPanel(
				getRemoteCollector(), currentRequests, getData().getJavaInformations());
		final DecimalFormat integerFormat = I18N.createIntegerFormat();
		final String text = I18N.getFormattedString("nb_requete_en_cours",
				integerFormat.format(currentRequests.size()))
				+ "     ";
		buttonsPanel.add(new JLabel(text), 0);
		buttonsPanel.add(detailsButton);

		return detailsPanel;
	}

	private MTableScrollPane<CounterRequest> createScrollPane() {
		final MTable<CounterRequest> table = getTable();
		final MTableScrollPane<CounterRequest> tableScrollPane = new MTableScrollPane<>(table);

		final boolean remoteUserDisplayed = isRemoteUserDisplayed();
		final boolean childHitsDisplayed = isChildHitsDisplayed();
		final boolean stackTraceEnabled = isStackTraceEnabled();
		addCustomColumn(I18N.getString("Thread"), new ThreadTableCellRenderer());
		if (remoteUserDisplayed) {
			addCustomColumn(I18N.getString("Utilisateur"), new RemoteUserTableCellRenderer());
		}
		table.addColumn("name", I18N.getString("Requete"));
		table.setColumnCellRenderer("name", new NameTableCellRenderer());

		addCustomColumn(I18N.getString("Duree_ecoulee"), new DurationTableCellRenderer());

		table.addColumn("mean", I18N.getString("Temps_moyen"));
		table.setColumnCellRenderer("mean", new IntegerTableCellRenderer());

		addCustomColumn(I18N.getString("Temps_cpu"), new CpuTableCellRenderer());

		table.addColumn("cpuTimeMean", I18N.getString("Temps_cpu_moyen"));
		table.setColumnCellRenderer("cpuTimeMean", new IntegerTableCellRenderer());
		// rq : tous ces contextes viennent du même compteur donc peu importe lequel des parentCounter
		if (childHitsDisplayed) {
			final String childCounterName = getData().getRootContexts().get(0).getParentCounter()
					.getChildCounterName();
			addCustomColumn(I18N.getFormattedString("hits_fils", childCounterName),
					new TotalChildHitsTableCellRenderer());

			addCustomColumn(I18N.getFormattedString("hits_fils_moyens", childCounterName),
					new ChildHitsMeanTableCellRenderer());

			addCustomColumn(I18N.getFormattedString("temps_fils", childCounterName),
					new TotalChildDurationsSumTableCellRenderer());

			addCustomColumn(I18N.getFormattedString("temps_fils_moyen", childCounterName),
					new ChildsDurationsMeanTableCellRenderer());
		}
		if (stackTraceEnabled) {
			addCustomColumn(I18N.getString("Methode_executee"),
					new ExecutedMethodTableCellRenderer());
		}

		return tableScrollPane;
	}

	private void addCustomColumn(String headerValue, TableCellRenderer tableCellRenderer) {
		final MTable<CounterRequest> table = getTable();
		final TableColumn tableColumn = new TableColumn(table.getColumnCount());
		tableColumn.setIdentifier(table.getColumnCount());
		tableColumn.setHeaderValue(headerValue);
		tableColumn.setCellRenderer(tableCellRenderer);
		table.addColumn(tableColumn);
	}

	private boolean isStackTraceEnabled() {
		return getData().getJavaInformations().isStackTraceEnabled();
	}

	private boolean isChildHitsDisplayed() {
		boolean childHitsDisplayed = false;
		for (final CounterRequestContext rootCurrentContext : getData().getRootContexts()) {
			if (rootCurrentContext.getParentCounter().getChildCounterName() != null) {
				// one root has child
				childHitsDisplayed = true;
				break;
			}
		}
		return childHitsDisplayed;
	}

	private boolean isRemoteUserDisplayed() {
		boolean remoteUserDisplayed = false;
		for (final CounterRequestContext context : getData().getRootContexts()) {
			if (context.getRemoteUser() != null) {
				remoteUserDisplayed = true;
				break;
			}
		}
		return remoteUserDisplayed;
	}

	CounterRequestContextData getData() {
		return data;
	}
}