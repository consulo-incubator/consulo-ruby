#Copyright 2000-2007 JetBrains s.r.o.
#
#Licensed under the Apache License, Version 2.0 (the "License");
#you may not use this file except in compliance with the License.
#You may obtain a copy of the License at
#
#http://www.apache.org/licenses/LICENSE-2.0
#
#Unless required by applicable law or agreed to in writing, software
#distributed under the License is distributed on an "AS IS" BASIS,
#WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#See the License for the specific language governing permissions and
#limitations under the License.  

# Created by IntelliJ IDEA.
# @author: oleg, Roman.Chernyatchik
# Date: Jun 13, 2007
require "settings"

require 'ftools'
require 'manually_fixed_definitions'
File.makedirs "#{$DIRECTORY}"

MAIN_FILE = File.new("#{$DIRECTORY}/#{$MAIN_FILE}", File::CREAT|File::TRUNC|File::RDWR)
MAIN_FILE << <<-DOC
=begin
 This is a machine generated main stub file using stdlib-doc
 Created on #{Time.now} by IntelliJ IDEA Ruby Plugin.

 This documentation uses content from the book "Programming Ruby - The Pragmatic Programmer's Guide"
 Copyright � 2001 by Addison Wesley Longman, Inc. This material may be distributed only subject to the terms and conditions set forth in the Open Publication License, v1.0 or later (the latest version is presently available at http://www.opencontent.org/openpub/)).
 Distribution of substantively modified versions of this document is prohibited without the explicit permission of the copyright holder.
 Distribution of the work or derivative of the work in any standard (paper) book form is prohibited unless prior permission is obtained from the copyright holder.

 This documentation uses content form the article http://en.wikibooks.org/wiki/Ruby_Programming/Syntax/Variables_and_Constants#Pre-defined_Variables
 Text is available under the GNU Free Documentation License. (http://en.wikibooks.org/wiki/GNU_Free_Documentation_License)
=end

DOC

MAIN_FILE << <<GLOBAL_VARS
# Exception information message set by 'raise'.
# This variable is thread local.
$! = Exception.new #{VALUE_UNKNOWN_COMMENT}

# Array of backtrace of the last exception thrown.
# This variable is thread local.
$@ = [] #{VALUE_UNKNOWN_COMMENT}

# String matched by last successful pattern match in this scope.
# Ruby interpreter sets this variable to 'nil' after an unsuccessful match.
# This variable is defined in current scope, thread local and read-only.
$& = "" #{VALUE_UNKNOWN_COMMENT}

# String to the left of last successful pattern match.
# Ruby interpreter sets this variable to 'nil' after an unsuccessful match.
# This variable is defined in current scope, thread local and read-only.
$` = "" #{VALUE_UNKNOWN_COMMENT}

# String to the right of last successful pattern match.
# Ruby interpreter sets this variable to 'nil' after an unsuccessful match.
# This variable is defined in current scope, thread local and read-only.
$' = "" #{VALUE_UNKNOWN_COMMENT}

# Last bracket(group) matched by last successful pattern match.
# Ruby interpreter sets this variable to 'nil' after an unsuccessful match.
# This variable is defined in current scope, thread local and read-only.
$+ = "" #{VALUE_UNKNOWN_COMMENT}

# 1st group of last successful pattern match.
# Ruby interpreter sets this variable to 'nil' after an unsuccessful match.
# This variable is defined in current scope, thread local and read-only.
$1 = "" #{VALUE_UNKNOWN_COMMENT}

# 2nd group of last successful pattern match.
# Ruby interpreter sets this variable to 'nil' after an unsuccessful match.
# This variable is defined in current scope, thread local and read-only.
$2 = "" #{VALUE_UNKNOWN_COMMENT}

# 3rd group of last successful pattern match.
# Ruby interpreter sets this variable to 'nil' after an unsuccessful match.
# This variable is defined in current scope, thread local and read-only.
$3 = "" #{VALUE_UNKNOWN_COMMENT}

# 4th group of last successful pattern match.
# Ruby interpreter sets this variable to 'nil' after an unsuccessful match.
# This variable is defined in current scope, thread local and read-only.
$4 = "" #{VALUE_UNKNOWN_COMMENT}

# 5th group of last successful pattern match.
# Ruby interpreter sets this variable to 'nil' after an unsuccessful match.
# This variable is defined in current scope, thread local and read-only.
$5 = "" #{VALUE_UNKNOWN_COMMENT}

# 6th group of last successful pattern match.
# Ruby interpreter sets this variable to 'nil' after an unsuccessful match.
# This variable is defined in current scope, thread local and read-only.
$6 = "" #{VALUE_UNKNOWN_COMMENT}

# 7th group of last successful pattern match.
# Ruby interpreter sets this variable to 'nil' after an unsuccessful match.
# This variable is defined in current scope, thread local and read-only.
$7 = "" #{VALUE_UNKNOWN_COMMENT}

# 8th group of last successful pattern match.
# Ruby interpreter sets this variable to 'nil' after an unsuccessful match.
# This variable is defined in current scope, thread local and read-only.
$8 = "" #{VALUE_UNKNOWN_COMMENT}

# 9th group of last successful pattern match.
# Ruby interpreter sets this variable to 'nil' after an unsuccessful match.
# This variable is defined in current scope, thread local and read-only.
$9 = "" #{VALUE_UNKNOWN_COMMENT}

# information about last match in the current scope. The variables
# $1 - $9,  $&, $` and $' are given from $~.
# Ruby interpreter sets this variable to 'nil' after an unsuccessful match.
# This variable is defined in current scope and thread local.
$~ = MatchData.new #{VALUE_UNKNOWN_COMMENT}

# Deprecated.
# The flag for case insensitive, nil by default. If flag is not 'nil'
# and not 'false' pattern matches and string comparisions will be case
# insensitive.
$= = Object.new #{VALUE_UNKNOWN_COMMENT}

# Input record separator, newline by default. This variable is used by
# Kernel#gets to separate records. If $/ is 'nil', Kernel#gets will read the
# entire file at once.
$/ = "" #{VALUE_UNKNOWN_COMMENT}

# Alias to $/.
$-0 = $/ #{VALUE_UNKNOWN_COMMENT}

# Output record separator string for Kernel#print and IO#write.
# Default is nil.
$\\ = "" #{VALUE_UNKNOWN_COMMENT}

# Output field separator string for Kernel#print and Array#join.
# Default is nil.
$, = "" #{VALUE_UNKNOWN_COMMENT}

# Default separator for String#split.
$; = "" #{VALUE_UNKNOWN_COMMENT}

# Alias to $;.
$-F = $; #{VALUE_UNKNOWN_COMMENT}

# Current input line number of last file that was read.
$. = 0 #{VALUE_UNKNOWN_COMMENT}

# Virtual concatenation file of files given on command line.
# $< supports File and Enumerable methods.
# This variable is read-only.
$< = File.new #{VALUE_UNKNOWN_COMMENT}

# Default output for Kernel#print, Kernel#printf. $stdout by default
$> = IO.new #{VALUE_UNKNOWN_COMMENT}

# Last line read by Kernel#gets or Kernel#readline.
# This variable is defined in current scope, thread local.
$_ = "" #{VALUE_UNKNOWN_COMMENT}

# Name of the script being executed. May be assignable.
$0 = "" #{VALUE_UNKNOWN_COMMENT}

# Command line arguments given for the script.
# Does not include interpreter arguments.
# This variable is read-only.
$* = [] #{VALUE_UNKNOWN_COMMENT}

# Process number of Ruby instance running this script.
# This variable is read-only.
$$ = 0 #{VALUE_UNKNOWN_COMMENT}

# Status of last executed child process.
# This variable is thread local and read-only.
$? = Process::Status.new #{VALUE_UNKNOWN_COMMENT}

# Load path for scripts and binary modules by load or require. You can
# append directory to load path using $: << dir_path.
# This variable is read-only.
$: = [] #{VALUE_UNKNOWN_COMMENT}

# Alias to $:.
$-I = $: #{VALUE_UNKNOWN_COMMENT}

# Alias to $:.
$LOAD_PATH  = $: #{VALUE_UNKNOWN_COMMENT}

# Module names loaded by require.
# Ruby interpreters have the following bug:
# 
# require "my/file"
# require "my/../my/file" #the same file as my/file
# p $"
#
# Produces: ["my/file.rb", "my/../my/file.rb"]
#
# This variable is read-only.
$" = [] #{VALUE_UNKNOWN_COMMENT}

# True if command-line option -d is set.
$DEBUG = Object.new #{VALUE_UNKNOWN_COMMENT}

# Alias to $DEBUG.
$-d = $DEBUG #{VALUE_UNKNOWN_COMMENT}

# Name of current input file from $<. Same as $<.filename.
# This variable is read-only.
$FILENAME = "" #{VALUE_UNKNOWN_COMMENT}

# Current standard error output.
$stderr = IO.new #{VALUE_UNKNOWN_COMMENT}

# Current standard input.
$stdin = IO.new #{VALUE_UNKNOWN_COMMENT}

# Current standard output. Assignment to $stdout is deprecated: use
# $stdout.reopen instead.
$stdout = IO.new #{VALUE_UNKNOWN_COMMENT}

# Determines current safe level. Safe level cannot be reduced by assignment.
# The default value of $SAFE is zero under most circumstances. The current
# value of $SAFE is inherited when new threads are created. However, within
# each thread, the value of $SAFE may be changed without affecting the value
# in other threads.
# This variable is thread local.
#
# $SAFE Constraints:
#   0    No checking of the use of externally supplied (tainted) data is performed.
#        This is Ruby's default mode.
#   >= 1 Ruby disallows the use of tainted data by potentially dangerous operations.
#   >= 2 Ruby prohibits the loading of program files from globally writable locations.
#   >= 3 All newly created objects are considered tainted.
#   >= 4 Ruby effectively partitions the running program in two. Nontainted objects may
#        not be modified. Typically, this will be used to create a sandbox: the program sets
#        up an environment using a lower $SAFE level, then resets $SAFE to 4 to prevent
#        subsequent changes to that environment.
$SAFE  = 0 #{VALUE_UNKNOWN_COMMENT}

# Verbose flag. Set by the -v, --version, -w, -W switch.
$VERBOSE  = Object.new #{VALUE_UNKNOWN_COMMENT}

# Alias to $VERBOSE.
$-v  = $VERBOSE #{VALUE_UNKNOWN_COMMENT}

# Alias to $VERBOSE.
$-w  = $VERBOSE #{VALUE_UNKNOWN_COMMENT}

# True if command-line option -a ("autosplit" mode) is set.
# This variable is read-only.
$-a = Object.new #{VALUE_UNKNOWN_COMMENT}

# If command-line option is set $_ will be split into $F.
$F = [] #{VALUE_UNKNOWN_COMMENT}

# If in-place-edit mode is set, this variable holds the extension, otherwise nil.
$-i = "" #{VALUE_UNKNOWN_COMMENT}

# Specifies multibyte code-set for strings and regular expressions.
# Equals to the -K command-line option.
# May be one of: u, U for UTF-8; or a, A, n, N for ASCII; e, E for EUC; s, S for SJIS
$-K = "" #{VALUE_UNKNOWN_COMMENT}

# True if command-line option -l is set ("line-ending processing" is on).
$-l = Object.new #{VALUE_UNKNOWN_COMMENT}

# True if command-line option -p is set ("loop" mode is on).
# This variable is read-only.
$-p  = Object.new #{VALUE_UNKNOWN_COMMENT}

# If a constant SCRIPT_LINES__ is defined as a Hash, then the source code of
# all files loaded by Kernel#load and Kernel#require will be stored in Hash.
#
# Example:
#   SCRIPT_LINES__ = {}
#   require 'my_file'
#   p SCRIPT_LINES__.keys
#   p SCRIPT_LINES__['./my_file']
#
#   produces:
#
#   ["./my_file.rb", "./other_file.rb"]
#   ["require 'other_file'\\n", "\\n"]
SCRIPT_LINES__ = nil #{VALUE_UNKNOWN_COMMENT}

GLOBAL_VARS

require 'set'

CORE_CLASSES = Set.new
VISITED = Set.new

INDENT = "    "

def create_indent indent
  print INDENT * indent
end

@number = 0
def require_constant someConstant, indent = 0

  someObject = eval "#{someConstant}"
  if someObject.kind_of? Class or someObject.kind_of? Module

    if VISITED.include? someObject
      create_indent indent
      puts "#{someConstant} already processed"
      return
    end
    VISITED.add someObject

    create_indent indent
    puts "Processing #{someConstant}"


    # Superclass processing
    if someObject.respond_to? :superclass
      superClass = someObject.superclass
      create_indent indent+1
      puts "Superclass #{superClass}"
      require_constant superClass, indent+1
    end

    # Mixins processing
    someObject.included_modules.each do |includedModule|
      create_indent indent+1
      puts "Mixin #{includedModule}"
      require_constant includedModule, indent+1
    end

    # Just after all the superclasses and includes we can require this constant

    create_indent indent
    puts "Done #{someConstant}. Number #{@number}"
    @number += 1
    name = "#{someConstant}".downcase.gsub /::.+/, ""
    MAIN_FILE << "require '#{name}'\n"
    CORE_CLASSES.add name.to_sym
  end

end

self.class.constants.each do |constant|
  require_constant constant
end

MAIN_FILE.close