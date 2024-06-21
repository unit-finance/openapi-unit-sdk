require 'fileutils'

source_dir = '../openapi-unit-sdk/schemas'
target_dir = '../openapi-unit-sdk/schemas_with_one_of'

exclusions = [
  'account.json',
  'application.json',
  'authorization_request.json',
  'card.json',
  'customer.json',
  'limits.json',
  'payment.json',
  'recurring_payment.json',
  'repayment.json',
  'transaction.json'
]

def copy_files(source, target, exclusions)
  FileUtils.mkdir_p(target) unless File.exist?(target)

  Dir.foreach(source) do |item|
    next if item == '.' or item == '..'
    next if exclusions.include?(item)

    source_path = File.join(source, item)
    target_path = File.join(target, item)

    if File.directory?(source_path)
      copy_files(source_path, target_path, exclusions)
    else
      FileUtils.cp(source_path, target_path)
    end
  end
end

copy_files(source_dir, target_dir, exclusions)

puts "Files copied successfully, excluding specified files."
