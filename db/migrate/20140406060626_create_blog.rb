class CreateBlog < ActiveRecord::Migration
  def up
    create_table :blogs do |t|
      t.integer :user_id
      t.string :hashtag
      t.string :title
      t.datetime :created_at
    end
  end

  def down
    drop_table :blogs
  end
end
