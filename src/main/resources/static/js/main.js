// js/main.js

// Global Variables
let stompClient = null;
let currentUser = null;
let currentChatSessionId = null;
let currentRecipientId = null;
let messagesPage = 0;
const messagesPageSize = 30;
let chatSessionsPage = 0;
const chatSessionsPageSize = 10;
let hasMoreMessages = true;

// Initialize DOM Elements
const allChat = document.getElementById('allChat');
const chatSec = document.getElementById('chatSec');
const messageInput = document.getElementById('messageInput');
const sendMessageBtn = document.getElementById('sendMessageBtn');
const sideMenu = document.getElementById('SideMenu');
const overlay = document.getElementById('overlay');

// Event Listener for Login
document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const username = document.getElementById('usernameInput').value.trim();
    if (username) {
        try {
            // Fetch user data
            const response = await fetch(`/user/by-username/${encodeURIComponent(username)}`);
            if (response.ok) {
                currentUser = await response.json();
                // Initialize WebSocket connection
                connectWebSocket();
                // Hide login, show chat interface
                document.getElementById('loginContainer').style.display = 'none';
                document.getElementById('mainContainer').style.display = 'flex';
                // Update current user info
                document.getElementById('currentUser').querySelector('img').src = 'images/img-04.jpg'; // Replace with actual user image
                document.getElementById('currentUsername').querySelector('span').textContent = currentUser.fullName;
                // Fetch chat sessions
                chatSessionsPage = 0; // Reset chat sessions page
                await loadChatSessions(chatSessionsPage);
            } else {
                alert('User not found.');
            }
        } catch (error) {
            console.error('Error fetching user data:', error);
            alert('An error occurred during login.');
        }
    }
});

// Function to Connect to WebSocket
function connectWebSocket() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, (frame) => {
        console.log('Connected:', frame);
        // Subscribe to user-specific topic
        stompClient.subscribe(`/user/${currentUser.id}/queue/messages`, onMessageReceived);
        // Notify server that user is online
        stompClient.send('/app/user.connectUser', {}, JSON.stringify(currentUser));
    }, (error) => {
        console.error('WebSocket connection error:', error);
    });
}

// Function to Handle Incoming Messages
function onMessageReceived(payload) {
    const message = JSON.parse(payload.body);
    switch (message.messageType) {
        case 'MESSAGE':
            handleIncomingChatMessage(message);
            break;
        case 'NOTIFICATION':
            handleNotification(message);
            break;
        case 'ONLINE_OFFLINE':
            handleUserStatusChange(message);
            break;
        default:
            console.warn('Unknown message type:', message.messageType);
    }
}

// Function to Handle Incoming Chat Messages
function handleIncomingChatMessage(message) {
    // Update chat session in the side panel
    updateChatSessionInList(message);
    // If the message is for the current chat session, display it
    if (currentChatSessionId === message.chatSessionId) {
        displayChatMessage(message);
    }
}

// Function to Handle Notifications
function handleNotification(message) {
    // Display notification to the user (you can customize this)
    alert(`Notification: ${message.content}`);
}

// Function to Handle User Status Changes
function handleUserStatusChange(message) {
    // Update user status in the chat list
    updateUserStatusInChatList(message.senderId, message.content);
}

// Function to Load Chat Sessions
async function loadChatSessions(page = 0) {
    try {
        console.log('Fetching chat sessions.');
        const response = await fetch(`/chat-session/all/${currentUser.id}?page=${page}&size=${chatSessionsPageSize}`);
        if (response.ok) {
            const chatSessions = await response.json();
            console.log('Chat Sessions Response:', chatSessions);
            displayChatSessions(chatSessions, page);

            // Open the first chat session by default on initial load
            if (page === 0) {
                const sessionsArray = chatSessions.content || chatSessions;
                if (sessionsArray.length > 0) {
                    // Open the first chat session
                    const firstSession = sessionsArray[0];
                    currentChatSessionId = firstSession.id;
                    messagesPage = 0; // Reset messages page
                    hasMoreMessages = true; // Reset hasMoreMessages
                    const partnerId = getChatPartnerId(firstSession.participantsIds);
                    currentRecipientId = partnerId; // Set recipient ID
                    loadChatMessages(firstSession.id);
                    // Update chat header
                    updateChatHeader(firstSession);
                } else {
                    // No chat sessions available
                    displayNoChatsMessage();
                }
            }
        } else {
            console.error('Failed to fetch chat sessions.');
        }
    } catch (error) {
        console.error('Error fetching chat sessions:', error);
    }
}

// Function to Display Chat Sessions
function displayChatSessions(chatSessions, page) {
    if (page === 0) {
        chatSec.innerHTML = ''; // Clear chat sessions on first load
    }

    // Access the array of chat sessions
    const sessionsArray = chatSessions.content || chatSessions;

    sessionsArray.forEach(session => {
        const chatItem = createChatItem(session);
        chatSec.appendChild(chatItem);
    });
}

// Function to Display "NO CHATS" Message
function displayNoChatsMessage() {
    allChat.innerHTML = ''; // Clear any existing messages

    const noChatsMsg = document.createElement('p');
    noChatsMsg.textContent = 'NO CHATS';
    noChatsMsg.classList.add('no-chats-message'); // Add a class for styling if needed

    allChat.appendChild(noChatsMsg);
}

// Helper Function to Create Chat Item
function createChatItem(session) {
    const chatItem = document.createElement('div');
    chatItem.classList.add('chat');
    chatItem.setAttribute('data-chat-session-id', session.id);
    chatItem.setAttribute('data-participant-id', getChatPartnerId(session.participantsIds));

    const img = document.createElement('img');
    img.src = 'images/default-user.jpg'; // Replace with actual user image if available

    const infoDiv = document.createElement('div');
    infoDiv.classList.add('chat-info');

    const nameP = document.createElement('p');
    nameP.classList.add('chat-name');
    nameP.textContent = 'Loading...'; // Placeholder, will update later

    const lastMessageP = document.createElement('p');
    lastMessageP.classList.add('chat-last-message');
    lastMessageP.textContent = session.lastMessage ? session.lastMessage.content : 'No messages yet';

    infoDiv.appendChild(nameP);
    infoDiv.appendChild(lastMessageP);

    const timeSpan = document.createElement('span');
    timeSpan.classList.add('chat-time');
    timeSpan.textContent = session.lastMessage ? new Date(session.lastMessage.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : '';

    // Create status indicator
    const statusIndicator = document.createElement('span');
    statusIndicator.classList.add('status-indicator');
    statusIndicator.style.backgroundColor = 'red'; // Default to offline (red)

    chatItem.appendChild(img);
    chatItem.appendChild(infoDiv);
    chatItem.appendChild(timeSpan);
    chatItem.appendChild(statusIndicator); // Append status indicator to chat item

    // Fetch partner user data to update name
    const partnerId = getChatPartnerId(session.participantsIds);
    fetch(`/user/${partnerId}`)
        .then(response => response.json())
        .then(user => {
            nameP.textContent = user.fullName;
            // Update image if user has one
            // img.src = user.profileImage || 'images/default-user.jpg';
            // Optionally, fetch and set initial online status
            // updateUserStatusInChatList(partnerId, user.status);
        })
        .catch(error => console.error('Error fetching user data:', error));

    chatItem.addEventListener('click', () => {
        currentChatSessionId = session.id;
        messagesPage = 0; // Reset messages page
        hasMoreMessages = true; // Reset hasMoreMessages
        currentRecipientId = partnerId; // Set recipient ID
        loadChatMessages(session.id);
        // Update chat header
        updateChatHeader(session);
        // Close Side Menu on Mobile
        if (window.innerWidth <= 1024) {
            sideMenu.classList.remove('active');
            overlay.classList.remove('active');
        }
    });

    return chatItem;
}

// Helper Function to Get Chat Partner ID
function getChatPartnerId(participantsIds) {
    return participantsIds.find(id => id !== currentUser.id);
}

// Function to Update Chat Header
function updateChatHeader(session) {
    const chatUserImage = document.getElementById('chatUserImage');
    const chatUserName = document.getElementById('chatUserName');
    const partnerId = getChatPartnerId(session.participantsIds);

    // Set the current recipient ID
    currentRecipientId = partnerId;

    // Fetch partner user data
    fetch(`/user/${partnerId}`)
        .then(response => response.json())
        .then(user => {
            chatUserImage.src = 'images/default-user.jpg'; // Replace with actual user image
            chatUserName.textContent = user.fullName;
        })
        .catch(error => console.error('Error fetching user data:', error));
}

// Function to Load Chat Messages
async function loadChatMessages(chatSessionId, page = 0) {
    try {
        const response = await fetch(`/message/${chatSessionId}?page=${page}&size=${messagesPageSize}`);
        if (response.ok) {
            const messages = await response.json();
            console.log('Messages Response:', messages);
            displayChatMessages(messages, page);
        } else {
            console.error('Failed to fetch chat messages.');
        }
    } catch (error) {
        console.error('Error fetching chat messages:', error);
    }
}

// Function to Display Chat Messages
function displayChatMessages(messages, page) {
    const messagesArray = messages.content || messages;

    if (page === 0) {
        allChat.innerHTML = '';
        // Reverse messages to display from oldest to newest
        messagesArray.reverse().forEach(message => {
            displayChatMessage(message, false);
        });
        // Scroll to bottom on first load
        allChat.scrollTop = allChat.scrollHeight;
    } else {
        // Save the current scroll height
        const currentScrollHeight = allChat.scrollHeight;
        const scrollTopBefore = allChat.scrollTop;

        // Reverse messages to display from oldest to newest
        messagesArray.reverse().forEach(message => {
            displayChatMessage(message, false, true);
        });

        // Adjust the scroll position
        const newScrollHeight = allChat.scrollHeight;
        allChat.scrollTop = newScrollHeight - currentScrollHeight + scrollTopBefore;
    }

    // Update hasMoreMessages flag
    if (messages.last !== undefined) {
        hasMoreMessages = !messages.last;
    }
}

// Function to Display a Single Chat Message
function displayChatMessage(message, scrollToBottom = true, prepend = false) {
    const chatBox = document.createElement('div');
    chatBox.classList.add('chat-box');
    if (message.senderId === currentUser.id) {
        chatBox.classList.add('my-message');
    }

    const img = document.createElement('img');
    img.src = message.senderId === currentUser.id ? 'images/img-04.jpg' : 'images/default-user.jpg'; // Update with actual images

    const chatTxt = document.createElement('div');
    chatTxt.classList.add('chat-txt');

    const h4 = document.createElement('h4');
    h4.textContent = message.senderId === currentUser.id ? 'You' : 'Partner';

    const span = document.createElement('span');
    span.textContent = ` • ${new Date(message.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}`;
    h4.appendChild(span);

    const p = document.createElement('p');
    p.textContent = message.content;

    chatTxt.appendChild(h4);
    chatTxt.appendChild(p);

    chatBox.appendChild(img);
    chatBox.appendChild(chatTxt);

    if (prepend) {
        // Insert at the top
        allChat.insertBefore(chatBox, allChat.firstChild);
    } else {
        // Append at the bottom
        allChat.appendChild(chatBox);
    }

    if (scrollToBottom) {
        allChat.scrollTop = allChat.scrollHeight;
    }
}

// Event Listener for Sending Messages
sendMessageBtn.addEventListener('click', sendMessage);
messageInput.addEventListener('keypress', (e) => {
    if (e.key === 'Enter') {
        sendMessage();
        e.preventDefault();
    }
});

// Function to Send Message
function sendMessage() {
    const content = messageInput.value.trim();
    if (content && currentChatSessionId && currentRecipientId) {
        const message = {
            chatSessionId: currentChatSessionId,
            senderId: currentUser.id,
            recipientId: currentRecipientId,
            content: content,
            timestamp: new Date(),
            messageType: 'MESSAGE'
        };
        stompClient.send('/app/chat', {}, JSON.stringify(message));
        messageInput.value = '';
        displayChatMessage(message);
        // Update last message in chat session
        updateChatSessionInList(message);
    }
}

// Function to Update Chat Session in List
function updateChatSessionInList(message) {
    const chatItems = chatSec.querySelectorAll('.chat');
    let chatItem = null;

    chatItems.forEach(item => {
        if (item.getAttribute('data-chat-session-id') === message.chatSessionId) {
            chatItem = item;
        }
    });

    if (chatItem) {
        // Update last message and time
        chatItem.querySelector('.chat-last-message').textContent = message.content;
        chatItem.querySelector('.chat-time').textContent = new Date(message.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
        // Move chat item to top
        chatSec.insertBefore(chatItem, chatSec.firstChild);
    } else {
        // If chat session not found, create a new one
        const newChatSession = {
            id: message.chatSessionId,
            participantsIds: [currentUser.id, message.senderId],
            lastMessage: { content: message.content, timestamp: message.timestamp }
        };
        const newChatItem = createChatItem(newChatSession);
        chatSec.insertBefore(newChatItem, chatSec.firstChild);
    }
}

// Function to Update User Status in Chat List
function updateUserStatusInChatList(userId, status) {
    const chatItems = chatSec.querySelectorAll('.chat');

    chatItems.forEach(item => {
        if (item.getAttribute('data-participant-id') === userId) {
            let statusIndicator = item.querySelector('.status-indicator');
            if (!statusIndicator) {
                // Create status indicator if it doesn't exist
                statusIndicator = document.createElement('span');
                statusIndicator.classList.add('status-indicator');
                item.appendChild(statusIndicator);
            }
            // Update the color based on the status
            statusIndicator.style.backgroundColor = status === 'ONLINE' ? 'green' : 'red';
        }
    });
}

// Toggle Side Menu for Mobile
document.getElementById('currentUser').addEventListener('click', () => {
    if (window.innerWidth <= 1024) {
        sideMenu.classList.add('active');
        overlay.classList.add('active');
    }
});

overlay.addEventListener('click', () => {
    sideMenu.classList.remove('active');
    overlay.classList.remove('active');
});

// Search Functionality
const searchInput = document.getElementById('searchInput');
const searchResults = document.getElementById('searchResults');

searchInput.addEventListener('input', () => {
    const query = searchInput.value.trim();
    if (query === '') {
        searchResults.innerHTML = '';
        chatSec.style.display = 'block';
    } else {
        chatSec.style.display = 'none';
        searchUsers(query);
    }
});

// Function to Search Users
async function searchUsers(query) {
    try {
        const response = await fetch(`/user/search/${encodeURIComponent(query)}`);
        if (response.ok) {
            const users = await response.json();
            displaySearchResults(users);
        } else {
            console.error('Failed to search users.');
        }
    } catch (error) {
        console.error('Error searching users:', error);
    }
}

// Function to Display Search Results
function displaySearchResults(users) {
    searchResults.innerHTML = '';
    if (users.length === 0) {
        const noResult = document.createElement('p');
        noResult.textContent = 'No users found.';
        noResult.classList.add('no-result');
        searchResults.appendChild(noResult);
    } else {
        users.forEach(user => {
            if (user.id !== currentUser.id) { // Exclude current user
                const resultItem = document.createElement('div');
                resultItem.classList.add('result-item');

                const img = document.createElement('img');
                img.src = 'images/default-user.jpg'; // Replace with user's image
                img.alt = user.fullName;

                const name = document.createElement('p');
                name.textContent = user.fullName;

                const buttons = document.createElement('div');
                buttons.classList.add('result-buttons');

                const addButton = document.createElement('button');
                addButton.classList.add('icon-btn');
                const addIcon = document.createElement('img');
                addIcon.src = 'images/add-icon.jpg'; // Replace with your add icon
                addIcon.alt = 'Add';
                addButton.appendChild(addIcon);

                const messageButton = document.createElement('button');
                messageButton.classList.add('icon-btn');
                const messageIcon = document.createElement('img');
                messageIcon.src = 'images/message.jpg'; // Replace with your message icon
                messageIcon.alt = 'Message';
                messageButton.appendChild(messageIcon);

                // Add event listener to message button
                messageButton.addEventListener('click', () => {
                    // Create or get chat session with the user
                    createOrGetChatSessionWithUser(user.id);
                    // Close search results and clear search input
                    searchResults.innerHTML = '';
                    searchInput.value = '';
                    chatSec.style.display = 'block';
                });

                // Add event listener to add button
                addButton.addEventListener('click', () => {
                    sendFriendRequest(user.id);
                });

                buttons.appendChild(addButton);
                buttons.appendChild(messageButton);

                resultItem.appendChild(img);
                resultItem.appendChild(name);
                resultItem.appendChild(buttons);

                searchResults.appendChild(resultItem);
            }
        });
    }
}

// Function to Create or Get Chat Session with User
async function createOrGetChatSessionWithUser(participantId) {
    try {
        const chatSession = {
            userId: currentUser.id,
            participantsIds: [currentUser.id, participantId],
            chatType: 'PERSONAL'
            // Other fields can be added if necessary
        };

        const response = await fetch(`/chat-session`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(chatSession)
        });

        if (response.ok) {
            const session = await response.json();
            // Add or update chat session in chat list
            updateChatSessions(session);
            // Open the chat session
            currentChatSessionId = session.id;
            messagesPage = 0; // Reset messages page
            hasMoreMessages = true; // Reset hasMoreMessages
            currentRecipientId = participantId; // Set the recipient ID
            loadChatMessages(session.id);
            // Update chat header
            updateChatHeader(session);
        } else {
            console.error('Failed to create or get chat session.');
        }
    } catch (error) {
        console.error('Error creating or getting chat session:', error);
    }
}

// Function to Update Chat Sessions
function updateChatSessions(session) {
    // Check if chat session already exists in the list
    const existingChatItem = chatSec.querySelector(`.chat[data-chat-session-id="${session.id}"]`);
    if (!existingChatItem) {
        const chatItem = createChatItem(session);
        chatSec.insertBefore(chatItem, chatSec.firstChild);
    }
}

// Function to Send Friend Request
async function sendFriendRequest(participantId) {
    try {
        const friendship = {
            userId1: currentUser.id,
            userId2: participantId,
            friendshipStatus: 'PENDING' // Assuming the initial status is PENDING
        };

        const response = await fetch(`/friendships`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(friendship)
        });

        if (response.ok) {
            alert('Friend request sent.');
        } else {
            console.error('Failed to send friend request.');
            alert('Failed to send friend request.');
        }
    } catch (error) {
        console.error('Error sending friend request:', error);
    }
}

// Infinite Scroll for M
