class CreatePermissions < ActiveRecord::Migration
  def up
    create_table :permissions do |t|
      t.integer :blog_id
      t.string :screen_name
      t.timestamps
    end
  end

  def down
    drop_table :permissions
  end
end
