# Consent Dialog Implementation

## Overview
This implementation adds a popup consent dialog to the Vaadin UI that appears when the MCP elicitation handler event is received.

## Components Added

### 1. ElicitationService.java
A Spring service that manages elicitation requests and responses between the MCP handler and the UI.

**Key Features:**
- Listener pattern for notifying UI components of elicitation requests
- CompletableFuture-based async communication
- Thread-safe request/response handling
- Asynchronous listener notification to prevent blocking UI thread
- 60-second timeout for user responses (defaults to DECLINE on timeout)

**Records:**
- `ElicitationRequest`: Wraps the MCP request and response future
- `ElicitationResponse`: Contains the user's action (ACCEPT/DECLINE) and message

### 2. McpClientHandlers.java (Modified)
Updated the `elicitationHandler` method to use `ElicitationService` instead of automatically returning ACCEPT.

**Changes:**
- Injected ElicitationService
- Calls `requestUserConsent()` which blocks until user responds
- Returns structured result based on user's choice

### 3. AiApplication.java (Existing - Already Configured)
The main application class already implements `AppShellConfigurator` and has the `@Push` annotation enabled.

**Key Points:**
- Already has `@Push` annotation for server-side push support
- Already implements `AppShellConfigurator`
- Enables real-time UI updates from background threads
- No modifications needed - Push is already configured

### 4. FlightBookingView.java (Modified)
Enhanced the UI to display consent dialogs when elicitation events are received.

**Changes:**
- Injected ElicitationService
- Registered listener for elicitation requests in constructor
- Added cleanup in `onDetach()` to remove listener
- Created `handleElicitationRequest()` to show dialog in UI thread
- Created `createConsentDialog()` to build the consent dialog
- Added comprehensive logging

**Dialog Features:**
- Modal dialog (blocks interaction with rest of UI)
- Cannot be closed by clicking outside or pressing ESC
- Displays request details
- Two buttons: Accept (primary) and Decline
- Thread-safe UI updates using `ui.access()`

## How It Works

1. **Elicitation Event**: When an MCP elicitation event occurs, the `@McpElicitation` annotated method in `McpClientHandlers` is called.

2. **Request User Consent**: The handler calls `ElicitationService.requestUserConsent()`, which:
   - Creates a CompletableFuture for the response
   - Asynchronously notifies all registered listeners (the UI) using CompletableFuture.runAsync()
   - Blocks the MCP handler thread waiting for the response (max 60 seconds), while the UI remains responsive

3. **Show Dialog**: The `FlightBookingView` listener receives the request and:
   - Ensures execution in the UI thread via `ui.access()`
   - Creates and opens a modal consent dialog

4. **User Response**: When the user clicks Accept or Decline:
   - The response is sent back via `elicitationService.respondToElicitation()`
   - The CompletableFuture completes
   - The dialog closes

5. **Return Response**: The blocked `requestUserConsent()` call returns with the user's response, which is then returned to the MCP framework.

## Testing

To test this implementation:

1. Start the flight-mcp-server
2. Start the flight-mcp-client application
3. Interact with the chat in a way that triggers an elicitation request
4. Observe the consent dialog appearing
5. Test both Accept and Decline actions

## Notes

- The dialog displays the request details using `toString()` which provides technical information
- If you want to customize the message displayed, you'll need to parse the `ElicitRequest` object and extract specific fields based on your MCP server's elicitation format
- The timeout of 60 seconds can be adjusted in `ElicitationService.requestUserConsent()`
- Multiple UI instances can register listeners simultaneously if needed
