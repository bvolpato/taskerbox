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
package org.brunocunha.taskerbox.gui;

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
import javax.swing.border.EmptyBorder;

import lombok.extern.log4j.Log4j;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.brunocunha.taskerbox.Taskerbox;
import org.brunocunha.taskerbox.core.TaskerboxChannel;
import org.brunocunha.taskerbox.core.TaskerboxChannelExecuteThread;
import org.brunocunha.taskerbox.gui.components.JCheckboxChannelControl;
import org.brunocunha.taskerbox.gui.event.RefreshChannelEvent;

@Log4j
public class TaskerboxControlFrame extends JFrame {

  /**
	 * 
	 */
  private static final long serialVersionUID = -1157187960493714189L;

  private static TaskerboxControlFrame controlFrame;

  private JPanel contentPane;
  private JTabbedPane tabbedPane;
  private Map<String, JPanel> channelPanelMap = new HashMap<String, JPanel>();

  private Taskerbox box;
  private JPanel panel_1;
  private JComboBox<String> comboBox;
  private JLabel lblNewLabel;
  private JPanel panel_2;
  private JButton btnPausarTodos;
  private JButton btnLigarTodos;

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

    setTitle("Taskerbox v0.1 by bruno.cunha ;)");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(100, 100, 600, 400);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    setContentPane(contentPane);
    contentPane.setLayout(new BorderLayout(0, 0));

    panel_1 = new JPanel();
    contentPane.add(panel_1, BorderLayout.NORTH);

    lblNewLabel = new JLabel("Logging Level:");
    panel_1.add(lblNewLabel);

    comboBox = new JComboBox<String>();
    comboBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Logger.getRootLogger().setLevel(Level.toLevel((String) comboBox.getSelectedItem()));
        log.info("Selected: " + comboBox.getSelectedItem());
      }
    });
    comboBox.setModel(new DefaultComboBoxModel<String>(new String[] {"DEBUG", "INFO", "WARN"}));
    comboBox.setSelectedIndex(1);
    panel_1.add(comboBox);

    tabbedPane = new JTabbedPane(JTabbedPane.TOP);
    contentPane.add(tabbedPane);



    panel_2 = new JPanel();
    contentPane.add(panel_2, BorderLayout.SOUTH);

    btnPausarTodos = new JButton("Pausar Todos");
    btnPausarTodos.addActionListener(new ActionListener() {
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
    panel_2.add(btnPausarTodos);

    btnLigarTodos = new JButton("Ligar Todos");
    btnLigarTodos.addActionListener(new ActionListener() {
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
    panel_2.add(btnLigarTodos);

    updateChannels();

  }

  public void actionPerformed(RefreshChannelEvent event) {
    this.updateChannels();
  }

  public synchronized void updateChannels() {
    log.debug("Calling updateChannels");

    if (box != null) {
      synchronized (box.getChannels()) {

        for (Entry<String, JPanel> panel : channelPanelMap.entrySet()) {
          panel.getValue().removeAll();
        }

        for (TaskerboxChannel<?> channel : box.getChannels()) {
          String groupName = channel.getGroupName();
          JPanel channelPanel = channelPanelMap.get(groupName);

          if (channelPanel == null) {
            channelPanel = new JPanel();
            channelPanel.setLayout(new GridLayout(0, 2, 0, 0));
            tabbedPane.addTab(groupName, null, channelPanel, null);
            channelPanelMap.put(groupName, channelPanel);
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

              log.info(checkboxThread.getText() + " - Pausado? "
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
    return box;
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

    public void mousePressed(MouseEvent e) {
      maybeShowPopup(e);
    }

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
        log.info("Building popup for " + channel.getId());

        this.popup = new JPopupMenu();
        JMenuItem menuItem = new JMenuItem("Executar agora");
        menuItem.addActionListener(new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            log.info("Forcing check on " + channel.getId());
            Thread t1 = new Thread() {
              public void run() {
                try {
                  // Just need to force if channel is currently paused
                  channel.check(channel.isPaused());
                } catch (Exception e1) {
                  e1.printStackTrace();
                }
              }
            };
            t1.start();
          }
        });

        this.popup.add(menuItem);

        JMenuItem menuProperties = new JMenuItem("Propriedades");
        menuProperties.addActionListener(new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent event) {
            log.info("Opening properties for " + channel.getId());

            TaskerboxChannelPropertiesFrame propertiesFrame;
            try {
              propertiesFrame = new TaskerboxChannelPropertiesFrame(frame, channel);
              propertiesFrame.setVisible(true);
            } catch (Exception e) {
              e.printStackTrace();
            }

          }
        });
        this.popup.add(menuProperties);


        if (channel.isRunning()) {
          JMenuItem menuKill = new JMenuItem("Matar Thread");
          menuKill.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
              log.info("Killing " + channel.getId());

              TaskerboxChannelExecuteThread thread = channel.getRunningThread();
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
