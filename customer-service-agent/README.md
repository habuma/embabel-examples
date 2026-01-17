# Embabel Customer Service Agent

[![Java CI with Gradle](https://github.com/habuma/embabel-customer-service-agent/actions/workflows/gradle.yml/badge.svg)](https://github.com/habuma/embabel-customer-service-agent/actions/workflows/gradle.yml)

This is an Embabel-based customer service agent.

The agent uses the GPT-OSS model running on a local Ollama. Therefore, before running
the agent, be sure to install Ollama and the GPT-OSS model (if you've not done so already).

It has two action methods:

- `processUserInput()` - This action processes the user input into a domain object that
  includes the order number and the user's inquiry.
- `classifyIssue()` - This action tried to classify the inquiry as MISSING_ITEM,
  DAMAGED_ITEM, WRONG_ITEM, DELAYED, REFUND_REQUEST, or OTHER
- `checkOrderStatus()` - This action looks up the order from the database.
- `determineResolutionPlan()` - This action uses the LLM to determine a resolution plan
  of REFUND, RESEND_ITEM, or CONTACT_CUSTOMER.
- `executeResolution()` - This action is intended to execute the plan, potentially
  updating the order's status in the database. But at this time it only returns a
  resolution confirmation domain object.
- `resolveIssue()` - This action achieves the goal of addressing the customer's inquiry
  and returns the final resolution response.

Running the agent
---
The agent uses the GPT-OSS model running on a local Ollama. Therefore, before running
the agent, be sure to install Ollama and the GPT-OSS model (if you've not done so already).

Run the agent as you would any Spring Boot application:

```shell
$ ./gradlew bootRun
```

Once running, it will expose  an MCP Server and an A2A agent listening on port 8080.

Test Data
---
The agent's database has been prepopulated with 3 orders:

- **112233** - Contains a SKU of "ABC-123", has already been shipped, is not refund eligible,
  but is resend eligible.
- **223344** - Contains a SKU of "DEF-456", has not yet been shipped, is refund eligible,
  but is not resend eligible.
- **334455** - Contains a SKU of "GHI-789", has already been shipped, and is not refund or
  resend eligible.

When testing the agent, you can submit inquiries such as "My order number is 112233. It
arrived damaged. Please send me a new one."

Accessing the agent through the MCP Inspector
---
Run the [MCP Inspector](https://modelcontextprotocol.io/docs/tools/inspector) (requires
that you have `npx` installed):

```shell
$ npx @modelcontextprotocol/inspector
```

Once the MCP Inspector starts it should open itself in a web browser. To access the
agent, enter/select the following in the left-hand form:

- Transport Type: Streamable HTTP
- URL: http://localhost:8080/mcp

Then click the "Connect" button. Assuming that the agent is running, the inspector
should connect.

Click on the "Tools" tab in the right panel and then click the "List Tools" button. You
should see a list of 4 tools: `helloBanner`, `inquiry`, `_confirm`, and
`submitFormAndResumeProcess`. The `inquiry` tool is the tool you'll use, so select it.

On the far right, you should see a form that includes two text areas labeled "orderNumber"
and "text". Enter an order number (e.g., "112233") and the inquiry text (e.g., "My order
arrived damaged. Please send a replacement.") Then click the "Run Tool" button. Once the
agent has processed the request, you should see the resolution text under "Tool Result".

Accessing the agent with Claude Code
---

In a shell, cd into a directory that will serve as the project directory.

```
$ mkdir dummy-project
$ cd dummy-project
```

Add the MCP server to Claude Code:

```
$ claude mcp add customer-service-agent --transport http http://localhost:8080/mcp
```

Open Claude Code shell and ask it to say hello to someone:
```
╭─── Claude Code v2.0.65 ─────────────────────────────────────────────────────────────────────────────────────────────────────────╮
│                                                 │ Tips for getting started                                                      │
│                  Welcome back!                  │ Run /init to create a CLAUDE.md file with instructions for Claude             │
│                                                 │ ─────────────────────────────────────────────────────────────────             │
│                     ▐▛███▜▌                     │ Recent activity                                                               │
│                    ▝▜█████▛▘                    │ No recent activity                                                            │
│                      ▘▘ ▝▝                      │                                                                               │
│                                                 │                                                                               │
│   Sonnet 4.5 · API Usage Billing · (personal)   │                                                                               │
│                      ~/tmp                      │                                                                               │
╰─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────╯

  /model to try Opus 4.5

> My order number is 112233. It arrived damaged. Please send me a new one.

⏺ I'll help you report this damaged order and request a replacement.

⏺ hello-agent - inquiry (MCP)(orderNumber: "112233", text: "Order arrived damaged. Customer is requesting a replacement.")
  ⎿  Hi there!

     Thank you for letting us know about the damaged item. We’ve taken care of it for you:
     … +12 lines (ctrl+o to expand)

⏺ Good news! Your replacement has been processed:
  ┌─────────────────┬─────────────────────┐
  │     Detail      │     Information     │
  ├─────────────────┼─────────────────────┤
  │ Order #         │ 112233              │
  ├─────────────────┼─────────────────────┤
  │ SKU             │ ABC-123             │
  ├─────────────────┼─────────────────────┤
  │ Status          │ Replacement shipped │
  ├─────────────────┼─────────────────────┤
  │ Confirmation ID │ RESEND-1234         │
  └─────────────────┴─────────────────────┘
  Your replacement should arrive in 3-5 business days, and you'll receive a tracking number once it's in the mail.
```
Note that before Claude Code will use the tool, it will ask if you want it to use that tool.

Accessing the agent with the A2A Inspector
---

Start the A2A Inspector per the instructions at https://github.com/a2aproject/a2a-inspector.
The A2A Inspector should start on port 5001. Open it in your web browser by entering
http://localhost:5001 into your browser's address bar.

Enter http://localhost:8080/a2a/.well-known/agent.json into the text box at the top and
then click the "Connect" button. You should see the agent card displayed below.

Scroll down to find the "Chat" section and type a message to send in the text field at
the bottom. For example, "My order number is 112233. It arrived damaged. Please send me a
new one.". Then click the "Send" button. The agent will begin processing the input. You will
see the A2A status updates appear in the chat session text box, including the final response
message in the session text box.

Accessing the agent with Claude Desktop
---

The MCP Server exposed by this agent employs the Streamable HTTP transport. At this time,
Claude Desktop does not directly support the Streamable HTTP transport. But you can
configure an STDIO server that proxies to the Streamable HTTP MCP Server.

Edit your Claude Desktop configuration file to include the following MCP Server:

```
{
  "mcpServers": {
    "customer-service-agent": {
      "command": "npx",
      "args": [
        "-y",
        "mcp-remote",
        "http://localhost:8080/mcp"
      ]
    }
  }
}
```

The location of your Claude Desktop configuration file will vary depending on your
operating system and username. On my Mac machine, it is at `/Users/habuma/Library/Application Support/Claude/claude_desktop_config.json`.
You can also find it by opening Settings in Claude Desktop, going to the "Developer"
tab, then clicking the "Edit Config" button near the top of the screen.

Once you've made the change to Claude Desktop's configuration, try it out by opening
a chat in Claude Desktop and asking it to resolve an order issue, such as "My order
number is 112233. It arrived damaged. Please send me a new one."











Run it as you would run any Spring Boot
application. For example, using the Gradle wrapper:

```sh
./gradlew bootRun
```

Once the agent is running, it will expose an MCP server on port 8080. You can poke at this
MCP server using the [MCP inspector](https://modelcontextprotocol.io/docs/tools/inspector)
(using the Streamable HTTP protocol at http://localhost:8080/mcp) or you can configure your favorite
LLM/Agent UI to use the MCP server.

To configure it in Claude Desktop, you'll need to run it through the MCP-Remote MCP server
which proxies through a local MCP server (since Claude Desktop does not yet support remote
MCP). To do that, add the following configuration to your Claude Desktop configuration:

```yaml
{
  "mcpServers": {
    "customer-service": {
      "command": "npx",
      "args": [
        "-y",
        "mcp-remote",
        "http://localhost:8080/mcp"
      ]
    }
  }
}
```

Then start a chat via the UI. You can try things like:

- My order arrived damaged. I want a refund.
- I ordered a red sweater, but received a green sweater. Can you please help?
- My order is a week late in arriving. Please help me.

When asked for an order number, numbers "112233", "223344", and "334455" are the order numbers
for three orders available in the database.

Note that (for the most part) the resolution that the agent arrives at is usually pretty
good. But, at this point I didn't spend much effort defining policies for the agent to
follow. As a result, it kinda wings it and sometimes responds with unusual resolutions.
Maybe I'll provide policies at some point in the future.

Accessing the agent with Goose
---

[Goose](https://block.github.io/goose/) is chat-oriented implementation of an agentic
loop. It can be used as either a desktop application (similar to Claude Desktop) or as a
CLI (similar to Cursor or Claude Code).

To use the Customer Service agent within Goose (desktop), configure it as an extension (e.g., 
as an MCP Server). The steps to do that are:

 - Open Goose
 - Click on the "Extensions" sidebar navigation on the left
 - Click the "+ Add custom extension" button
 - Enter the following values into the form:
   - **Extension name**: Customer Service
   - Type: Streamable HTTP
   - Description: Enables customer service functions such helping a user with issues with 
     their order.
   - Endpoint: http://localhost:8080/mcp
 - Leave all other fields as they are
 - Click the "Add Extension" button

Then navigate to Goose's chat functionality by clicking on "Chat" in the sidebar navigation.
Try inquiring about an order by asking something like "My order number is 223344. It hasn't 
arrived. I want a refund." If all goes well, you should see the results once the Customer
Service agent has achieved its goal.