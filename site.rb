require 'sinatra'
require 'thin'

get '/' do
    erb index
end
