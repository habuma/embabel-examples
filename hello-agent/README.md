Hello Agent
===
This is a relatively simple "Hello" example agent.

It has two action methods:

- `extractPerson()` - This action extracts the name of a person to say hello to from the 
  `UserInput`. This action is only necessary to handle natural language user input as
  would be provided from the Embabel shell. When using the agent as an MCP server, it is
  unnecessary.
- `sayHello()` - This is the goal-achieving main action. Given a `Person` object it
  produces a `Greeting` response.

Running the agent
---
The agent uses the GPT-OSS model running on a local Ollama. Therefore, before running
the agent, be sure to install Ollama and the GPT-OSS model (if you've not done so already).

Run the agent as you would any Spring Boot application: 

```shell
$ ./gradlew bootRun
```

Once running, it will expose  an MCP Server and an A2A agent listening on port 8080.

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
should see a list of 4 tools: `helloBanner`, `sayHello`, `_confirm`, and 
`submitFormAndResumeProcess`. The `sayHello` tool is the tool you'll use, so select it.

On the far right, you should see a form that includes a text area labeled as "name".
Enter a name into the form and then click the "Run Tool" button. Once the agent has
processed the request, you should see the greeting under "Tool Result".

Accessing the agent with Claude Code
---

In a shell, cd into a directory that will serve as the project directory.

```
$ mkdir dummy-project
$ cd dummy-project
```

Add the MCP server to Claude Code:

```
$ claude mcp add hello-agent --transport http http://localhost:8080/mcp
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

> Say hello to Craig

⏺ I'll say hello to Craig using the greeting tool.

⏺ hello-agent - Person_sayHello (MCP)(name: "Craig")
  ⎿  Greeting[greetingText=Hello, Craig!]

⏺ Hello, Craig!
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
the bottom. For example, "Say hello to Craig". Then click the "Send" button. The agent
will begin processing the input. You will see the A2A status updates appear in the chat
session text box, including the greeting message in the final update.

Accessing the agent with Claude Desktop
---

The MCP Server exposed by this agent employs the Streamable HTTP transport. At this time,
Claude Desktop does not directly support the Streamable HTTP transport. But you can 
configure an STDIO server that proxies to the Streamable HTTP MCP Server.

Edit your Claude Desktop configuration file to include the following MCP Server:

```
{
  "mcpServers": {
    "hello-agent": {
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
a chat in Claude Desktop and asking it to say hello to someone.