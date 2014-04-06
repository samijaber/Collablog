require 'sinatra'
require "sinatra/activerecord"

#CONFIG
set :database, "sqlite3:///db/db.sqlite3"
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
    pass if request.path_info =~ /^\/auth\//
    redirect to("/auth/twitter") unless current_user
end
#ROUTES
get "/auth/twitter/callback" do
    session[:uid] = env["omniauth.auth"]["uid"]
    redirect to("/")
end
get "/auth/failure" do
    "AUTH ERROR"
end
get "/" do
    "AUTH WORKS"
end

