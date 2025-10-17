package com.vaadin.lab.ui;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.lab.ai.CustomerSupportAssistant;
import com.vaadin.lab.services.BookingDetails;
import com.vaadin.lab.services.FlightBookingService;

import java.util.UUID;

@Route("")
public class FlightBookingView extends SplitLayout {


    private final FlightBookingService flightBookingService;
    private final CustomerSupportAssistant assistant;
    private Grid<BookingDetails> grid;
    private final String chatId = UUID.randomUUID().toString();

    public FlightBookingView(
        FlightBookingService flightBookingService,
        CustomerSupportAssistant assistant
    ) {
        this.flightBookingService = flightBookingService;
        this.assistant = assistant;
        setSizeFull();
        setOrientation(Orientation.HORIZONTAL);
        setSplitterPosition(30);

        addToPrimary(createChatLayout());
        addToSecondary(createGrid());

        updateBookings();
    }


    private Component createChatLayout() {
        var chatLayout = new VerticalLayout();
        var messageList = new MessageList();
        var messageInput = new MessageInput();

        messageList.setMarkdown(true);
        chatLayout.setPadding(false);

        messageInput.setWidthFull();
        messageInput.addSubmitListener(e -> handleMessageInput(e.getValue(), messageList));

        chatLayout.addAndExpand(messageList);
        chatLayout.add(messageInput);
        return chatLayout;
    }

    private Component createGrid() {
        grid = new Grid<>(BookingDetails.class);
        grid.setSizeFull();
        grid.setColumns("bookingNumber", "firstName", "lastName", "date", "bookingStatus", "from", "to", "seatNumber", "bookingClass");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        return grid;
    }


    private void handleMessageInput(String userMessage, MessageList messageList) {
        var userMessageItem = new MessageListItem(userMessage, null, "You");
        userMessageItem.setUserColorIndex(1);
        messageList.addItem(userMessageItem);

        var responseItem = new MessageListItem(assistant.chat(chatId, userMessage), null, "Assistant");
        responseItem.setUserColorIndex(2);
        messageList.addItem(responseItem);

        updateBookings();
    }

    private void updateBookings() {
        grid.setItems(flightBookingService.getBookings());
    }

}
