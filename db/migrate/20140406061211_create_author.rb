class CreateAuthor < ActiveRecord::Migration
  def up
    create_table :authors, :id => false do |t|
      t.integer :id
      t.string :nickname
    end
  end

  def down
    drop_table :authors
  end
end
