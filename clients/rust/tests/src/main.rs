use clap::{Parser, Subcommand};
use gamma_rust_client::{
    api::GammaClient,
    config::GammaConfig,
    oauth::{gamma_init_auth, GammaState},
};

#[derive(Parser, Debug)]
struct Args {
    #[arg(long, env = "GAMMA_CLIENT_ID")]
    gamma_client_id: String,

    #[arg(long, env = "GAMMA_CLIENT_SECRET")]
    gamma_client_secret: String,

    #[arg(long, env = "GAMMA_API_KEY")]
    gamma_api_key: String,

    #[arg(long, env = "GAMMA_URL")]
    gamma_url: String,

    #[arg(long, env = "GAMMA_REDIRECT_URL")]
    gamma_redirect_url: String,

    #[command(subcommand)]
    action: TestAction,

    /// Weather or not to print (almost) complete retrieved content.
    #[arg(long, short)]
    debug: bool,
}

#[derive(Subcommand, Debug)]
enum TestAction {
    Init,
    Finish { code: String },
    Api,
}

#[tokio::main]
async fn main() {
    dotenvy::dotenv().ok();

    let args = Args::parse();
    run(args).await.expect("failed to run test with argument");
}

async fn run(args: Args) -> eyre::Result<()> {
    let gamma_config = GammaConfig {
        gamma_client_id: args.gamma_client_id,
        gamma_client_secret: args.gamma_client_secret,
        gamma_redirect_uri: args.gamma_redirect_url,
        gamma_url: args.gamma_url,
        scopes: "openid profile email".into(),
        gamma_api_key: args.gamma_api_key,
    };

    match args.action {
        TestAction::Init => init(&gamma_config),
        TestAction::Finish { code } => finish(args.debug, &gamma_config, code).await,
        TestAction::Api => api(args.debug, &gamma_config).await,
    }
}

fn init(gamma_config: &GammaConfig) -> eyre::Result<()> {
    let init = gamma_init_auth(gamma_config)?;
    println!("To perform the test, go to the link below and when authorization is complete (redirected back to the configured redirect URL), extract the `code` query param and use for the next step.\n\n Link: {}", init.redirect_to);
    Ok(())
}

async fn finish(debug: bool, gamma_config: &GammaConfig, code: String) -> eyre::Result<()> {
    // Note: We are testing the rust client, not gamma itself so we don't care about the state
    // parameter in this case. In real auth flows it is important to verify that the state returned
    // by gamma is identical to the state provided when initializing the auth flow.
    let state = "IGNORED".to_string();

    let gamma_state: GammaState = state.clone().into();

    println!("Retrieving auth token");
    let auth_token = gamma_state
        .gamma_callback_params(gamma_config, state, code)
        .await?;

    println!("Retrieved auth token complete, using it to retrieve userinfo");

    let user_info = auth_token.get_current_user(gamma_config).await?;
    println!("Retrieving user info was successful");

    if debug {
        println!("\tUserinfo: {user_info:?}");
    }

    Ok(())
}

async fn api(debug: bool, gamma_config: &GammaConfig) -> eyre::Result<()> {
    let client = GammaClient::new(gamma_config);

    // Get groups
    println!("Retrieving groups...");
    let groups = client.get_groups().await?;
    println!("Successfully retrieved {} groups", groups.len());
    if debug {
        for group in groups.iter() {
            println!("  - {{{group:?}}}");
        }
    }

    println!("\n\n");

    // Get super groups
    println!("Retrieving super groups...");
    let super_groups = client.get_super_groups().await?;
    println!("Successfully retrieved {} super groups", super_groups.len());
    if debug {
        for super_group in super_groups.iter() {
            println!("  - {{{super_group:?}}}");
        }
    }

    println!("\n\n");

    // Get authorities
    println!("Retrieving authorities...");
    let authorities = client.get_authorities().await?;
    println!("Successfully retrieved {} authorities", authorities.len());
    if debug {
        for authority in authorities.iter() {
            println!("  - {{{authority:?}}}");
        }
    }

    println!("\n\n");

    // Get users
    println!("Retrieving users...");
    let users = client.get_users().await?;
    println!("Successfully retrieved {} users", users.len());
    if debug {
        for user in users.iter() {
            println!("  - {{{user:?}}}");
        }
    }

    println!("\n\n");

    if let Some(user) = users.first() {
        // Get user (again)
        println!("Retrieving user by ID {}", user.id);
        let user = client.get_user(&user.id).await?;
        println!("Successfully retrieved user by its ID");
        if debug {
            println!("  - {{{user:?}}}");
        }

        // Get groups for user
        println!("Retrieving groups for a specific user... Picking the first user received in the previous call ().");
        let user_groups = client.get_groups_for_user(&user.id).await?;
        println!(
            "Successfully retrieved {} groups for the user",
            user_groups.len()
        );
        if debug {
            for group in user_groups.iter() {
                println!("  - {{{group:?}}}");
            }
        }

        println!("\n\n");

        // Get authorities for user
        println!("Retrieving authorities for a specific user... Picking the first user received in the get users call ().");
        let user_authorities = client.get_authorities_for_user(&user.id).await?;
        println!(
            "Successfully retrieved {} authorities for the user",
            user_authorities.len()
        );
        if debug {
            for authority in user_authorities.iter() {
                println!("  - {{{authority:?}}}");
            }
        }
    } else {
        println!("No users received, skipping endpoints requiring a user ID")
    }

    Ok(())
}
