require 'sinatra'
require "sinatra/activerecord"

set :database, "sqlite3:///db/db.sqlite3"

get '/' do
    erb :index
end

