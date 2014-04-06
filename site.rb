require "bundler/setup"
require "sinatra"
require "sinatra/activerecord"
require "omniauth-twitter"

@@splash_path = "/"
@@auth_path = "/auth/twitter"
@@app_path = "/app"
@@new_blog_path = "/blog/new"
@@blog_path = "/blog/"

#CONFIG
set :database, "sqlite3:///db/db.sqlite3"
set :port, 80
set :bind, '0.0.0.0'
enable :sessions

configure do
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
  session[:info] = env["omniauth.auth"]["info"]
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
  @blogs = Blog.all
  erb :app
end

get "/blog/new" do
  redirect to(@@auth_path) unless current_user
  erb :blog_new
end

post "/blog/new" do
  redirect to(@@auth_path) unless current_user
  blog = Blog.new
  blog.title = params[:title]
  blog.hashtag = params[:hashtag]
  blog.user_id = session[:uid]
  blog.created_at = DateTime.now
  blog.save
  redirect to(@@blog_path+blog.id.to_s)
end

get "/blog/:id" do
  @blog = Blog.find params[:id]
  erb :blog_show
end

class Blog < ActiveRecord::Base
  has_many :authors
end

class Author < ActiveRecord::Base
end
