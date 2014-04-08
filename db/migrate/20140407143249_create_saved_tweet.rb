class CreateSavedTweet < ActiveRecord::Migration
  def up
    create_table :saved_tweet do |t|
      t.integer :blog_id
      t.string :text
      t.string :url
      t.string :screen_name
      t.datetime :created_at
    end
  end

  def down
    drop_table :saved_tweets
  end
end
