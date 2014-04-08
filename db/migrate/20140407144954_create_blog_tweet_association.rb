class CreateBlogTweetAssociation < ActiveRecord::Migration
  def up
    create_table :blogs_saved_tweets, :index => false do |t|
      t.belongs_to :blog
      t.belongs_to :saved_tweet
    end 
  end

  def down
    drop_table :blogs_saved_tweets
  end
end
