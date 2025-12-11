package com.juatsapp.app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Ventana principal de Juatsapp.
 * Muestra chats, mensajes, envíos y actualización en tiempo real.
 */
public class MainChatFrame extends JFrame {

    private final User currentUser;
    private final ChatDao chatDao;
    private final MessageDao messageDao;
    private final UserDao userDao;

    private JList<Chat> chatList;
    private DefaultListModel<Chat> chatListModel;
    private JPanel messagesPanel;
    private JScrollPane messagesScroll;
    private JTextField messageInput;

    // Header de chat (nueva mejora)
    private JLabel lblChatName;

    // Para tiempo real
    private Chat currentChat;
    private Thread messageListenerThread;
    private boolean listening = true;

    public MainChatFrame(User currentUser) {
        this.currentUser = currentUser;
        this.chatDao = new ChatDao();
        this.messageDao = new MessageDao();
        this.userDao = new UserDao();
        initComponents();
        loadChats();
        startMessageListener();
    }

    private void initComponents() {
        setTitle("Juatsapp - Chats de " + currentUser.getPhone());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 760);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // -----------------------------------------
        // 🔷 BARRA SUPERIOR
        // -----------------------------------------
        JPanel topBar = new JPanel(new BorderLayout());
        JLabel lblUser = new JLabel("Conectado como: " + currentUser.getPhone());

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnProfile = new JButton("Perfil");
        btnProfile.addActionListener(e -> new ProfileFrame(this, currentUser).setVisible(true));

        JButton btnNewChat = new JButton("Nuevo chat");
        btnNewChat.addActionListener(this::onNewChat);

        actionsPanel.add(btnProfile);
        actionsPanel.add(btnNewChat);

        topBar.add(lblUser, BorderLayout.WEST);
        topBar.add(actionsPanel, BorderLayout.EAST);

        topBar.setBackground(new Color(0x075E54));
        lblUser.setForeground(Color.WHITE);
        actionsPanel.setOpaque(false);

        mainPanel.add(topBar, BorderLayout.NORTH);

        // -----------------------------------------
        // 🔷 PANEL IZQUIERDO - LISTA DE CHATS
        // -----------------------------------------
        chatListModel = new DefaultListModel<>();
        chatList = new JList<>(chatListModel);
        chatList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        chatList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {

                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Chat chat) {

                    String title = "Chat";
                    List<String> participants = chat.getParticipantIds();

                    if (participants != null) {
                        for (String uid : participants) {
                            if (!uid.equals(currentUser.getId())) {
                                User other = userDao.findById(uid);
                                if (other != null) title = other.getPhone();
                                break;
                            }
                        }
                    }
                    label.setText(title);
                }
                return label;
            }
        });

        chatList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Chat selected = chatList.getSelectedValue();
                if (selected != null) {
                    currentChat = selected;
                    updateChatHeader(selected);
                    loadMessages(selected);
                }
            }
        });

        JScrollPane chatsScroll = new JScrollPane(chatList);
        chatsScroll.setPreferredSize(new Dimension(200, 0));
        mainPanel.add(chatsScroll, BorderLayout.WEST);

        // -----------------------------------------
        // 🔷 PANEL DERECHO (MENSAJES + HEADER)
        // -----------------------------------------
        JPanel rightPanel = new JPanel(new BorderLayout());

        // HEADER DEL CHAT (Mejora #1)
        JPanel chatHeader = new JPanel(new BorderLayout());
        chatHeader.setBackground(new Color(0x128C7E));
        chatHeader.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        lblChatName = new JLabel("");
        lblChatName.setForeground(Color.WHITE);
        lblChatName.setFont(lblChatName.getFont().deriveFont(Font.BOLD, 15f));

        chatHeader.add(lblChatName, BorderLayout.WEST);
        rightPanel.add(chatHeader, BorderLayout.NORTH);

        // PANEL DE MENSAJES
        messagesPanel = new JPanel();
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
        messagesPanel.setBackground(new Color(0xE5DDD5));

        messagesScroll = new JScrollPane(messagesPanel);
        messagesScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        rightPanel.add(messagesScroll, BorderLayout.CENTER);

        // -----------------------------------------
        // 🔷 INPUT DE MENSAJES
        // -----------------------------------------
        JPanel inputPanel = new JPanel(new BorderLayout());
        messageInput = new JTextField();
        JButton sendButton = new JButton("Enviar");

        sendButton.addActionListener(this::onSendMessage);

        inputPanel.add(messageInput, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        inputPanel.setBackground(new Color(0xF5F5F5));

        mainPanel.add(rightPanel, BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        chatList.setBackground(new Color(0xECEFF1));

        setContentPane(mainPanel);
    }

    // ------------------------------------------------------------
    // 🔥 MEJORA: Mostrar nombre del contacto arriba del chat
    // ------------------------------------------------------------
    private void updateChatHeader(Chat chat) {
        String otherName = "";

        for (String uid : chat.getParticipantIds()) {
            if (!uid.equals(currentUser.getId())) {
                User u = userDao.findById(uid);
                if (u != null) otherName = u.getPhone();
                break;
            }
        }

        lblChatName.setText(otherName);
    }

    // ------------------------------------------------------------
    // Cargar Chats
    // ------------------------------------------------------------
    private void loadChats() {
        chatListModel.clear();
        List<Chat> chats = chatDao.findChatsForUser(currentUser.getId());
        chats.forEach(chatListModel::addElement);
    }

    // ------------------------------------------------------------
    // Cargar Mensajes
    // ------------------------------------------------------------
    private void loadMessages(Chat chat) {
        if (chat == null) return;

        messagesPanel.removeAll();
        List<Message> messages = messageDao.getMessagesForChat(chat.getId());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        for (Message msg : messages) {

            boolean isOwn = msg.getSenderId() != null && msg.getSenderId().equals(currentUser.getId());
            String hora = msg.getTimestamp() != null ? timeFormat.format(msg.getTimestamp()) : "";

            JPanel linePanel = new JPanel();
            linePanel.setLayout(new BoxLayout(linePanel, BoxLayout.X_AXIS));
            linePanel.setOpaque(false);

            JPanel bubble = new JPanel();
            bubble.setLayout(new BoxLayout(bubble, BoxLayout.Y_AXIS));
            bubble.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

            // 🔥 MEJORA: colores más bonitos
            Color bubbleColor = isOwn ? new Color(220, 248, 198) : new Color(255, 255, 255);
            bubble.setBackground(bubbleColor);

            JLabel textLabel = new JLabel("<html>" + msg.getText() + "</html>");
            JLabel metaLabel = new JLabel((isOwn ? "Tú" : "Contacto") + " · " + hora);

            metaLabel.setFont(metaLabel.getFont().deriveFont(Font.PLAIN, 9f));
            metaLabel.setForeground(new Color(80, 80, 80));

            bubble.add(textLabel);
            bubble.add(metaLabel);

            if (isOwn) {
                linePanel.add(Box.createHorizontalGlue());
                linePanel.add(bubble);
            } else {
                linePanel.add(bubble);
                linePanel.add(Box.createHorizontalGlue());
            }

            messagesPanel.add(linePanel);
            messagesPanel.add(Box.createVerticalStrut(4));
        }

        messagesPanel.revalidate();
        messagesPanel.repaint();

        SwingUtilities.invokeLater(this::smoothScrollToBottom);
    }

    // ------------------------------------------------------------
    // 🔥 MEJORA: Scroll suave
    // ------------------------------------------------------------
    private void smoothScrollToBottom() {
        JScrollBar bar = messagesScroll.getVerticalScrollBar();
        Timer timer = new Timer(5, null);

        timer.addActionListener(e -> {
            int newVal = bar.getValue() + 20;
            if (newVal >= bar.getMaximum()) {
                bar.setValue(bar.getMaximum());
                timer.stop();
            } else {
                bar.setValue(newVal);
            }
        });

        timer.start();
    }

    // ------------------------------------------------------------
    // Enviar mensaje
    // ------------------------------------------------------------
    private void onSendMessage(ActionEvent e) {
        if (currentChat == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un chat primero.");
            return;
        }

        String text = messageInput.getText().trim();

        // 🔥 MEJORA: evitar mensajes vacíos
        if (text.isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "Escribe un mensaje primero.",
                    "Mensaje vacío",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        messageDao.addMessage(currentChat.getId(), currentUser.getId(), text);

        messageInput.setText("");
        loadMessages(currentChat);
    }

    // ------------------------------------------------------------
    // 🔥 Listener en tiempo real
    // ------------------------------------------------------------
    private void startMessageListener() {
        messageListenerThread = new Thread(() -> {

            int lastCount = 0;

            while (listening) {
                try {
                    if (currentChat != null) {
                        List<Message> msgs = messageDao.getMessagesForChat(currentChat.getId());

                        if (msgs.size() != lastCount) {
                            SwingUtilities.invokeLater(() -> loadMessages(currentChat));
                            lastCount = msgs.size();
                        }
                    }

                    Thread.sleep(1000);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        messageListenerThread.start();
    }

    // ------------------------------------------------------------
    // Crear chat nuevo
    // ------------------------------------------------------------
    private void onNewChat(ActionEvent e) {
        String phone = JOptionPane.showInputDialog(this, "Teléfono del otro usuario:");
        if (phone == null || phone.isBlank()) return;

        if (!phone.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "Teléfono inválido.");
            return;
        }

        User other = userDao.findByPhone(phone);
        if (other == null) {
            JOptionPane.showMessageDialog(this, "No existe ese usuario.");
            return;
        }

        if (other.getId().equals(currentUser.getId())) {
            JOptionPane.showMessageDialog(this, "No puedes chatear contigo mismo.");
            return;
        }

        List<String> part = new ArrayList<>();
        part.add(currentUser.getId());
        part.add(other.getId());

        chatDao.createChat(part, currentUser.getId());
        loadChats();
    }

    // ------------------------------------------------------------
    // Detener hilo
    // ------------------------------------------------------------
    @Override
    public void dispose() {
        listening = false;
        super.dispose();
    }
}
