class CreateBlog < ActiveRecord::Migration
  def up
    create_table :blogs do |t|
      t.string :screen_name
      t.string :hashtag
      t.string :title
      t.timestamps
    end
  end

  def down
    drop_table :blogs
  end
end
