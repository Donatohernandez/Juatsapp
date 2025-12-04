package com.juatsapp.app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Ventana principal de Juatsapp. Muestra la lista de chats del usuario
 * autenticado y los mensajes del chat seleccionado, además de permitir
 * el envío de nuevos mensajes.
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

    public MainChatFrame(User currentUser) {
        this.currentUser = currentUser;
        this.chatDao = new ChatDao();
        this.messageDao = new MessageDao();
        this.userDao = new UserDao();
        initComponents();
        loadChats();
    }

    private void initComponents() {
        setTitle("Juatsapp - Chats de " + currentUser.getPhone());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Un poco más ancho para dar espacio a lista y mensajes
        setSize(520, 760);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Barra superior con información del usuario y botones de acciones
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

        // Panel izquierdo: lista de chats
        chatListModel = new DefaultListModel<>();
        chatList = new JList<>(chatListModel);
        chatList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Renderer personalizado para mostrar el teléfono del otro participante
        chatList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Chat) {
                    Chat chat = (Chat) value;
                    String title = "Chat";
                    java.util.List<String> participants = chat.getParticipantIds();
                    if (participants != null) {
                        for (String userId : participants) {
                            if (!userId.equals(currentUser.getId())) {
                                User other = userDao.findById(userId);
                                if (other != null && other.getPhone() != null) {
                                    title = other.getPhone();
                                }
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
                    loadMessages(selected);
                }
            }
        });

        JScrollPane chatsScroll = new JScrollPane(chatList);
        chatsScroll.setPreferredSize(new Dimension(200, 0));
        mainPanel.add(chatsScroll, BorderLayout.WEST);

        // Panel derecho: mensajes en forma de burbujas
        JPanel rightPanel = new JPanel(new BorderLayout());

        messagesPanel = new JPanel();
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
        messagesPanel.setBackground(new Color(0xE5DDD5)); // parecido a fondo de WhatsApp

        messagesScroll = new JScrollPane(messagesPanel);
        messagesScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        rightPanel.add(messagesScroll, BorderLayout.CENTER);

        // Barra inferior de entrada que abarca todo el ancho
        JPanel inputPanel = new JPanel(new BorderLayout());
        messageInput = new JTextField();
        JButton sendButton = new JButton("Enviar");
        sendButton.addActionListener(this::onSendMessage);

        inputPanel.add(messageInput, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        mainPanel.add(rightPanel, BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        // Colores de fondo tipo app de mensajería
        Color bgChats = new Color(0xECEFF1);
        mainPanel.setBackground(new Color(0xE5DDD5));
        rightPanel.setBackground(new Color(0xE5DDD5));
        chatList.setBackground(bgChats);
        inputPanel.setBackground(new Color(0xF5F5F5));

        setContentPane(mainPanel);
    }

    /**
     * Carga en la lista los chats en los que participa el usuario actual.
     */
    private void loadChats() {
        chatListModel.clear();
        List<Chat> chats = chatDao.findChatsForUser(currentUser.getId());
        for (Chat chat : chats) {
            chatListModel.addElement(chat);
        }
    }

    /**
     * Carga los mensajes del chat seleccionado en el área de texto.
     */
    private void loadMessages(Chat chat) {
        messagesPanel.removeAll();
        List<Message> messages = messageDao.getMessagesForChat(chat.getId());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        for (Message msg : messages) {
            boolean isOwn = msg.getSenderId() != null && msg.getSenderId().equals(currentUser.getId());
            String hora = msg.getTimestamp() != null ? timeFormat.format(msg.getTimestamp()) : "";

            // Línea horizontal que contiene la burbuja y el espacio vacío
            JPanel linePanel = new JPanel();
            linePanel.setLayout(new BoxLayout(linePanel, BoxLayout.X_AXIS));
            linePanel.setOpaque(false);

            // Burbuja de mensaje
            JPanel bubble = new JPanel();
            bubble.setLayout(new BoxLayout(bubble, BoxLayout.Y_AXIS));
            bubble.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));

            Color bubbleColor = isOwn ? new Color(0xDCF8C6) : Color.WHITE;
            bubble.setBackground(bubbleColor);

            JLabel textLabel = new JLabel("<html>" + msg.getText() + "</html>");
            JLabel metaLabel = new JLabel((isOwn ? "Tú" : "Contacto") + " · " + hora);
            metaLabel.setFont(metaLabel.getFont().deriveFont(Font.PLAIN, 10f));
            metaLabel.setForeground(Color.DARK_GRAY);

            bubble.add(textLabel);
            bubble.add(metaLabel);

            if (isOwn) {
                // Espacio a la izquierda y burbuja pegada a la derecha
                linePanel.add(Box.createHorizontalGlue());
                linePanel.add(bubble);
            } else {
                // Burbuja pegada a la izquierda y espacio a la derecha
                linePanel.add(bubble);
                linePanel.add(Box.createHorizontalGlue());
            }

            messagesPanel.add(linePanel);
            messagesPanel.add(Box.createVerticalStrut(4));
        }

        messagesPanel.revalidate();
        messagesPanel.repaint();
        // Scroll al final para ver el mensaje más reciente
        SwingUtilities.invokeLater(() -> messagesScroll.getVerticalScrollBar().setValue(messagesScroll.getVerticalScrollBar().getMaximum()));
    }

    /**
     * Envía un nuevo mensaje al chat actualmente seleccionado.
     */
    private void onSendMessage(ActionEvent e) {
        Chat selected = chatList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un chat para enviar mensajes.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String text = messageInput.getText().trim();
        if (text.isEmpty()) {
            return;
        }

        messageDao.addMessage(selected.getId(), currentUser.getId(), text);
        messageInput.setText("");
        loadMessages(selected);
    }

    /**
     * Crea un nuevo chat con otro usuario identificado por su teléfono.
     */
    private void onNewChat(ActionEvent e) {
        String phone = JOptionPane.showInputDialog(this, "Teléfono del otro usuario:");
        if (phone == null) {
            return; // usuario canceló
        }

        phone = phone.trim();
        if (phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar un teléfono.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!phone.matches("\\d{8,15}")) {
            JOptionPane.showMessageDialog(this, "El teléfono debe contener solo dígitos y tener entre 8 y 15 caracteres.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User other = userDao.findByPhone(phone);
        if (other == null) {
            JOptionPane.showMessageDialog(this, "No existe un usuario con ese teléfono.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (other.getId().equals(currentUser.getId())) {
            JOptionPane.showMessageDialog(this, "No puede crear un chat consigo mismo.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<String> participants = new ArrayList<>();
        participants.add(currentUser.getId());
        participants.add(other.getId());

        chatDao.createChat(participants, currentUser.getId());
        loadChats();
    }
}
