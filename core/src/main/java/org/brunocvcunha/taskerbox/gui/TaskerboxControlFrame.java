/**
 * Copyright (C) 2015 Bruno Candido Volpato da Cunha (brunocvcunha@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.brunocvcunha.taskerbox.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.brunocvcunha.taskerbox.Taskerbox;
import org.brunocvcunha.taskerbox.core.TaskerboxChannel;
import org.brunocvcunha.taskerbox.core.TaskerboxChannelExecuteThread;
import org.brunocvcunha.taskerbox.gui.components.JCheckboxChannelControl;
import org.brunocvcunha.taskerbox.gui.event.RefreshChannelEvent;

import lombok.extern.log4j.Log4j;

@Log4j
public class TaskerboxControlFrame extends JFrame {

  /**
	 *
	 */
  private static final long serialVersionUID = -1157187960493714189L;

  private static TaskerboxControlFrame controlFrame;

  private JPanel contentPane;
  private JTabbedPane tabbedPane;
  private Map<String, JPanel> channelPanelMap = new HashMap<>();

  private Taskerbox box;
  private JPanel panel_1;
  private JComboBox<String> comboBox;
  private JLabel lblNewLabel;
  private JPanel panel_2;
  private JButton btnPauseAll;
  private JButton btnResumeAll;

  private TaskerboxControlFrame frame = this;

  public static TaskerboxControlFrame buildInstance(final Taskerbox box) {
    controlFrame = new TaskerboxControlFrame(box);
    return getInstance();
  }

  public static boolean hasFrame() {
    return controlFrame != null;
  }

  public static TaskerboxControlFrame getInstance() {
    if (controlFrame == null) {
      throw new RuntimeException("Control Frame not created");
    }

    return controlFrame;
  }

  /**
   * Create the frame.
   */
  private TaskerboxControlFrame(final Taskerbox box) {
    this.box = box;

    setTitle("Taskerbox v0.1 by @brunocvcunha ;)");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(100, 100, 600, 400);
    this.contentPane = new JPanel();
    this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    setContentPane(this.contentPane);
    this.contentPane.setLayout(new BorderLayout(0, 0));

    this.panel_1 = new JPanel();
    this.contentPane.add(this.panel_1, BorderLayout.NORTH);

    this.lblNewLabel = new JLabel("Logging Level:");
    this.panel_1.add(this.lblNewLabel);

    this.comboBox = new JComboBox<>();
    this.comboBox.addActionListener(new ActionListener() {
      @Override
    public void actionPerformed(ActionEvent e) {
        Logger.getRootLogger().setLevel(Level.toLevel((String) TaskerboxControlFrame.this.comboBox.getSelectedItem()));
        log.info("Selected: " + TaskerboxControlFrame.this.comboBox.getSelectedItem());
      }
    });
    this.comboBox.setModel(new DefaultComboBoxModel<>(new String[] {"DEBUG", "INFO", "WARN"}));
    this.comboBox.setSelectedIndex(1);
    this.panel_1.add(this.comboBox);

    this.tabbedPane = new JTabbedPane(SwingConstants.TOP);
    this.contentPane.add(this.tabbedPane);



    this.panel_2 = new JPanel();
    this.contentPane.add(this.panel_2, BorderLayout.SOUTH);

    this.btnPauseAll = new JButton("Pause All");
    this.btnPauseAll.addActionListener(new ActionListener() {
      @Override
    public void actionPerformed(ActionEvent e) {
        if (box != null) {
          synchronized (box.getChannels()) {
            for (TaskerboxChannel<?> channel : box.getChannels()) {
              channel.setPaused(true);
            }
          }

          updateChannels();
        }
      }
    });
    this.panel_2.add(this.btnPauseAll);

    this.btnResumeAll = new JButton("Resume All");
    this.btnResumeAll.addActionListener(new ActionListener() {
      @Override
    public void actionPerformed(ActionEvent e) {
        if (box != null) {
          synchronized (box.getChannels()) {
            for (TaskerboxChannel<?> channel : box.getChannels()) {
              channel.setPaused(false);
            }
          }

          updateChannels();
        }
      }
    });
    this.panel_2.add(this.btnResumeAll);

    updateChannels();

  }

  public void actionPerformed(RefreshChannelEvent event) {
    this.updateChannels();
  }

  public synchronized void updateChannels() {
    log.debug("Calling updateChannels");

    if (this.box != null) {
      synchronized (this.box.getChannels()) {

        for (Entry<String, JPanel> panel : this.channelPanelMap.entrySet()) {
          panel.getValue().removeAll();
        }

        for (TaskerboxChannel<?> channel : this.box.getChannels()) {
          String groupName = channel.getGroupName();
          JPanel channelPanel = this.channelPanelMap.get(groupName);

          if (channelPanel == null) {
            channelPanel = new JPanel();
            channelPanel.setLayout(new GridLayout(0, 2, 0, 0));
            this.tabbedPane.addTab(groupName, null, channelPanel, null);
            this.channelPanelMap.put(groupName, channelPanel);
          }

          final JCheckboxChannelControl checkboxThread = new JCheckboxChannelControl();

          if (channel.isDaemon()) {
            checkboxThread.setText(channel.getDisplayName() + " (service)");
          } else {
            checkboxThread
                .setText(channel.getDisplayName() + " ("
                    + (channel.isRunning() ? "running" : "wait") + " #" + channel.getCheckCount()
                    + ")");
          }
          checkboxThread.setChannel(channel);
          checkboxThread.setSelected(!channel.isPaused());
          checkboxThread.addMouseListener(new PopupListener(channel));
          checkboxThread.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
              log.info(e);

              if (checkboxThread.isSelected()) {
                checkboxThread.getChannel().setPaused(false);
              } else {
                checkboxThread.getChannel().setPaused(true);
              }

              log.info(checkboxThread.getText() + " - Paused? "
                  + checkboxThread.getChannel().isPaused());

            }
          });

          channelPanel.add(checkboxThread);
        }
      }

      this.repaint();

    }
  }

  public Taskerbox getBox() {
    return this.box;
  }

  public void setBox(Taskerbox box) {
    this.box = box;
  }

  class PopupListener extends MouseAdapter {
    private TaskerboxChannel<?> channel;
    private JPopupMenu popup;

    public PopupListener(TaskerboxChannel<?> channel) {
      this.channel = channel;
    }

    @Override
    public void mousePressed(MouseEvent e) {
      maybeShowPopup(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      maybeShowPopup(e);
    }

    private void maybeShowPopup(MouseEvent e) {
      if (e.isPopupTrigger()) {
        getPopup().show(e.getComponent(), e.getX(), e.getY());
      }
    }

    private JPopupMenu getPopup() {
      if (this.popup == null) {
        log.info("Building popup for " + this.channel.getId());

        this.popup = new JPopupMenu();
        JMenuItem menuItem = new JMenuItem("Executar agora");
        menuItem.addActionListener(new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            log.info("Forcing check on " + PopupListener.this.channel.getId());
            Thread t1 = new Thread() {
              @Override
            public void run() {
                try {
                  // Just need to force if channel is currently paused
                  PopupListener.this.channel.check(PopupListener.this.channel.isPaused());
                } catch (Exception e1) {
                  e1.printStackTrace();
                }
              }
            };
            t1.start();
          }
        });

        this.popup.add(menuItem);

        JMenuItem menuProperties = new JMenuItem("Properties");
        menuProperties.addActionListener(new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent event) {
            log.info("Opening properties for " + PopupListener.this.channel.getId());

            TaskerboxChannelPropertiesFrame propertiesFrame;
            try {
              propertiesFrame = new TaskerboxChannelPropertiesFrame(TaskerboxControlFrame.this.frame, PopupListener.this.channel);
              propertiesFrame.setVisible(true);
            } catch (Exception e) {
              e.printStackTrace();
            }

          }
        });
        this.popup.add(menuProperties);


        if (this.channel.isRunning()) {
          JMenuItem menuKill = new JMenuItem("Kill Thread");
          menuKill.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
              log.info("Killing " + PopupListener.this.channel.getId());

              TaskerboxChannelExecuteThread thread = PopupListener.this.channel.getRunningThread();
              if (thread != null) {
                thread.interrupt();

                if (!thread.isInterrupted()) {
                  thread.interrupt();
                }
              }

            }
          });
          this.popup.add(menuKill);
        }

      }

      return this.popup;
    }
  }

}
