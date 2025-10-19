package com.vaadin.lab.ui;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.lab.ai.CustomerSupportAssistant;
import com.vaadin.lab.ai.ElicitationService;
import com.vaadin.lab.services.BookingDetails;
import com.vaadin.lab.services.FlightBookingService;
import io.modelcontextprotocol.spec.McpSchema.ElicitResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Route("")
public class FlightBookingView extends SplitLayout {

    private static final Logger logger = LoggerFactory.getLogger(FlightBookingView.class);


    private final FlightBookingService flightBookingService;
    private final CustomerSupportAssistant assistant;
    private final ElicitationService elicitationService;
    private Grid<BookingDetails> grid;
    private final String chatId = UUID.randomUUID().toString();
    private Consumer<ElicitationService.ElicitationRequest> elicitationListener;

    public FlightBookingView(
        FlightBookingService flightBookingService,
        CustomerSupportAssistant assistant,
        ElicitationService elicitationService
    ) {
        this.flightBookingService = flightBookingService;
        this.assistant = assistant;
        this.elicitationService = elicitationService;
        
        setSizeFull();
        setOrientation(Orientation.HORIZONTAL);
        setSplitterPosition(30);

        addToPrimary(createChatLayout());
        addToSecondary(createGrid());

        updateBookings();
        
        // Register listener for elicitation requests
        elicitationListener = this::handleElicitationRequest;
        elicitationService.addListener(elicitationListener);
    }
    
    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        // Clean up listener when view is detached
        if (elicitationListener != null) {
            elicitationService.removeListener(elicitationListener);
        }
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

        // Add a temporary "thinking" message
        var thinkingItem = new MessageListItem("_Thinking..._", null, "Assistant");
        thinkingItem.setUserColorIndex(2);
        messageList.addItem(thinkingItem);

        // Get UI reference for async updates
        UI ui = getUI().orElse(null);
        if (ui == null) {
            logger.error("UI not available for async chat");
            return;
        }

        // Execute chat asynchronously to keep UI responsive
        CompletableFuture.runAsync(() -> {
            try {
                logger.info("Starting async chat operation");
                String response = assistant.chat(chatId, userMessage);
                logger.info("Chat response received");
                
                // Update UI in the UI thread
                ui.access(() -> {
                    try {
                        logger.info("Updating UI with chat response");
                        // Create new items list without the thinking message
                        var items = new ArrayList<>(messageList.getItems());
                        items.remove(thinkingItem);
                        
                        // Add the actual response
                        var responseItem = new MessageListItem(response, null, "Assistant");
                        responseItem.setUserColorIndex(2);
                        items.add(responseItem);
                        
                        // Update the message list
                        messageList.setItems(items);
                        
                        // Update bookings grid
                        updateBookings();
                        
                        // Force push to client
                        ui.push();
                        logger.info("UI updated successfully");
                    } catch (Exception ex) {
                        logger.error("Error updating UI", ex);
                    }
                });
            } catch (Exception e) {
                logger.error("Error in async chat", e);
                ui.access(() -> {
                    try {
                        // Create new items list without the thinking message
                        var items = new ArrayList<>(messageList.getItems());
                        items.remove(thinkingItem);
                        
                        var errorItem = new MessageListItem("_Error: " + e.getMessage() + "_", null, "Assistant");
                        errorItem.setUserColorIndex(2);
                        items.add(errorItem);
                        
                        messageList.setItems(items);
                        
                        // Force push to client
                        ui.push();
                    } catch (Exception ex) {
                        logger.error("Error updating UI with error message", ex);
                    }
                });
            }
        });
    }

    private void updateBookings() {
        grid.setItems(flightBookingService.getBookings());
    }

    private void handleElicitationRequest(ElicitationService.ElicitationRequest request) {
        logger.info("Elicitation request received in FlightBookingView (instance: {})", System.identityHashCode(this));
        
        // Get UI reference
        UI ui = getUI().orElse(null);
        if (ui == null) {
            logger.error("UI is not available for FlightBookingView instance: {}", System.identityHashCode(this));
            // Auto-decline if UI not available
            elicitationService.respondToElicitation(request.responseFuture(), 
                new ElicitationService.ElicitationResponse(ElicitResult.Action.DECLINE, "UI not available"));
            return;
        }
        
        logger.info("UI details - ID: {}, isAttached: {}, session: {}", 
            ui.getUIId(), ui.isAttached(), ui.getSession() != null);
        
        // Check if this view is currently attached and visible
        if (!isAttached()) {
            logger.warn("FlightBookingView is not attached, skipping dialog");
            return;
        }
        
        logger.info("Opening consent dialog in UI thread for UI: {}", ui.getUIId());
        
        // Open dialog directly (UI thread is now free thanks to async chat)
        ui.access(() -> {
            try {
                logger.info("Inside ui.access() - Opening consent dialog");
                Dialog consentDialog = createConsentDialog(request);
                consentDialog.open();
                logger.info("Dialog opened successfully");
            } catch (Exception e) {
                logger.error("Error in dialog operation", e);
                // Auto-decline on error
                elicitationService.respondToElicitation(request.responseFuture(), 
                    new ElicitationService.ElicitationResponse(ElicitResult.Action.DECLINE, "Error: " + e.getMessage()));
            }
        });
        
        logger.info("Dialog opening scheduled");
    }

    private Dialog createConsentDialog(ElicitationService.ElicitationRequest request) {
        Dialog dialog = new Dialog();
        dialog.setModal(true);
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);
        dialog.setWidth("500px");

        // Header
        H3 header = new H3("Consent Required");
        header.getStyle().set("margin", "0");

        // Content
        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(true);
        
        // Display the request details
        String displayMessage = "The assistant is requesting your consent to proceed with an action.";
        
        // Try to extract meaningful information from the request
        if (request.request() != null) {
            displayMessage += "\n\nRequest details: " + request.request().message().toString();
        }
        
        Paragraph message = new Paragraph(displayMessage);
        message.getStyle().set("margin", "0");
        message.getStyle().set("white-space", "pre-wrap");
        
        content.add(message);

        // Buttons
        Button acceptButton = new Button("Accept", e -> {
            ElicitationService.ElicitationResponse response = 
                new ElicitationService.ElicitationResponse(ElicitResult.Action.ACCEPT, "yes");
            elicitationService.respondToElicitation(request.responseFuture(), response);
            dialog.close();
        });
        acceptButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button declineButton = new Button("Decline", e -> {
            ElicitationService.ElicitationResponse response = 
                new ElicitationService.ElicitationResponse(ElicitResult.Action.DECLINE, "no");
            elicitationService.respondToElicitation(request.responseFuture(), response);
            dialog.close();
        });

        HorizontalLayout buttonLayout = new HorizontalLayout(acceptButton, declineButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.setWidthFull();

        // Layout
        VerticalLayout dialogLayout = new VerticalLayout(header, content, buttonLayout);
        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(true);
        
        dialog.add(dialogLayout);

        return dialog;
    }

}
