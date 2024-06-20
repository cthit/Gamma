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

    /// Weather or not to print retrieved content.
    #[arg(long)]
    debug_print: bool,
}

#[derive(Subcommand, Debug)]
enum TestAction {
    Init,
    Finish { code: String, state: String },
    API,
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
        scopes: "openid profile".into(),
        gamma_api_key: args.gamma_api_key,
    };

    match args.action {
        TestAction::Init => init(&gamma_config),
        TestAction::Finish { code, state } => {
            finish(args.debug_print, &gamma_config, code, state).await
        }
        TestAction::API => api(args.debug_print, &gamma_config).await,
    }
}

fn init(gamma_config: &GammaConfig) -> eyre::Result<()> {
    let init = gamma_init_auth(gamma_config)?;
    println!("To perform the test, go to the provided link and then use the code together with this state: \"{:?}\". Link: {}", init.state, init.redirect_to);
    Ok(())
}

async fn finish(
    debug: bool,
    gamma_config: &GammaConfig,
    code: String,
    state: String,
) -> eyre::Result<()> {
    // Note: Doesn't actually validate the state :sweat_smile:
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

    println!("Retrieving groups...");
    let groups = client.get_groups().await?;
    println!("Successfully retrieved {} groups", groups.len());

    if debug {
        for group in groups.into_iter() {
            println!("  - {{{group:?}}}");
        }
    }

    Ok(())
}
