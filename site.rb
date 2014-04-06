require "bundler/setup"
require "sinatra"
require "sinatra/activerecord"
require "omniauth-twitter"

@@splash_path = "/"
@@auth_path = "/auth/twitter"
@@app_path = "/app"

#CONFIG
set :database, "sqlite3:///db/db.sqlite3"
set :port, 80
set :bind, '0.0.0.0'

configure do
  enable :sessions
  use OmniAuth::Builder do
    provider :twitter, ENV["CONSUMER_KEY"], ENV["CONSUMER_SECRET"]
  end
end

#HELPERS
helpers do
  def current_user
    !session[:uid].nil?
  end
end

#BEFORE
before do
  pass if request.path_info =~ /^\/auth\// and request.path != "/"
  redirect to("/auth/twitter") unless current_user
end

#ROUTES
get "/auth/twitter/callback" do
  session[:uid] = env["omniauth.auth"]["uid"]
  redirect to(@@app_path)
end

get "/auth/failure" do
  "AUTH ERROR"
end

get "/" do
  erb :splash
end

get "/app" do
  redirect to(@@auth_path) unless current_user
  "AUTH WORKS"
end

